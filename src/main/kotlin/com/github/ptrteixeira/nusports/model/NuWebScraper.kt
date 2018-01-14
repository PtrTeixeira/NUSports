/* Released under the MIT license, $YEAR */

package com.github.ptrteixeira.nusports.model

import com.github.ptrteixeira.nusports.model.ApplicationModelModule.Companion.MODEL_COROUTINE_POOL
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import org.apache.logging.log4j.LogManager
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.util.Objects
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Scrape the CAA site, in particular, to load information relevant to Northeastern.

 *
 *

 *
 * It is extremely tightly tied to the actual structure of the CAA site, but I couldn't find a
 * REST endpoint or anything similar that would allow me to trivially extract the information that I
 * needed. So it just scrapes straight off of HTML, which works until the CAA changes how their site
 * is laid out again.

 * @author Peter
 */
internal class NuWebScraper @Inject
constructor(
    private val standingsCache: MutableMap<String, List<Standing>>,
    private val scheduleCache: MutableMap<String, List<Match>>,
    private val documentSource: DocumentSource,
    @Named(MODEL_COROUTINE_POOL) private val context: CoroutineContext = CommonPool
) : WebScraper {

    @Throws(ConnectionFailureException::class)
    override suspend fun getStandings(sport: String): List<Standing> {
        val result = standingsCache[sport]

        if (result == null) {
            logger.debug("Standings for {} not found in cache. Connecting to external source", sport)
            val computed = loadStandings(sport)
            standingsCache[sport] = computed
            return computed
        } else {
            return result
        }
    }

    @Throws(ConnectionFailureException::class)
    override suspend fun getSchedule(sport: String): List<Match> {
        val result = scheduleCache[sport]

        if (result == null) {
            logger.debug("Schedule for {} not found in cache. Connecting to external source", sport)
            val computed = loadSchedule(sport)
            scheduleCache[sport] = computed
            return computed
        } else {
            return result
        }
    }

    override val selectableSports: List<String> = listOf(
        "Baseball",
        "Field Hockey",
        "Men's Basketball",
        "Women's Basketball",
        "Men's Soccer",
        "Women's Soccer",
        "Softball",
        "Volleyball"
    )

    override fun clearCache(sport: String) {
        Objects.requireNonNull(sport)

        logger.info("Clearing the cache for \"{}\"", sport)
        this.scheduleCache.remove(sport)
        this.standingsCache.remove(sport)
    }

    @Throws(ConnectionFailureException::class)
    private suspend fun loadStandings(sport: String): List<Standing> {
        try {
            val queryPath = "http://caasports.com/standings.aspx?path=${sportToPath(sport)}"
            logger.debug("Making query to path {}", queryPath)
            val doc = async(context) { documentSource.load(queryPath) }

            val rows = doc.await().getElementsByClass("default_dgrd") // list of <table>
                .first() // <table>
                .getElementsByTag("tbody") // list of <tbody>
                .first() // <tbody>
                .children() // list of <tr>

            val result = rows
                .drop(1) // Drop the header from the table
                .map(standingsParser(sport))

            logger.debug("Found standings data {} on the web for {}", result, sport)
            return result
        } catch (iex: IOException) {
            logger.warn("Connection failure getting standings data", iex)
            throw ConnectionFailureException("Failed to connect to the internet.")
        }
    }

    @Throws(ConnectionFailureException::class)
    private suspend fun loadSchedule(sport: String): List<Match> {
        try {
            logger.debug("Schedule for \"{}\" not found in cache; connecting to external source", sport)
            val queryPath = "http://caasports.com/calendar.aspx"
            val doc = async(context) { documentSource.load(queryPath) }

            val nuGames = this.extractSport(doc.await().getElementsByClass("school_3"), sport)
            val results = nuGames
                .map(this::parseMatch)

            logger.debug("Found schedule data {} on the web for {}. Writing to cache.", results, sport)
            return results
        } catch (iex: IOException) {
            logger.trace("Connection failure getting schedule data", iex)
            throw ConnectionFailureException("Failed to connect to the internet.")
        }
    }

    // Extract all elements in e1 with a class of sport
    private fun extractSport(el: Elements, sport: String): Elements
        = el.select(".${sportToClass(sport)}")

    // Convert the given string sport into an HTML class
    // Used in extracting data from the calendar
    fun sportToClass(sport: String): String {
        logger.debug("Finding CSS class for \"{}\"", sport)

        return when (sport) {
            "Baseball" -> "sport_1"
            "Field Hockey" -> "sport_3"
            "Men's Basketball" -> "sport_6"
            "Men's Soccer" -> "sport_8"
            "Softball" -> "sport_9"
            "Women's Basketball" -> "sport_13"
            "Women's Soccer" -> "sport_16"
            "Volleyball" -> "sport_17"
            else -> ""
        }

        /*
     * class school_3 = Northeastern
     *
     * class sport_1 = Baseball
     * class sport_3 = Field Hockey
     * class sport_4 = Football
     * class sport_6 = Men's Basketball
     * class sport_18 = Men's XC
     * class sport_20 = Men's Golf
     * class sport_7 = Men's Lacrosse
     * class sport_8 = Men's Soccer
     * class sport_22 = Men's Swimming
     * class sport_24 = Men's Tennis
     * class sport_26 = Men's Track and Field
     * class sport_9 = Softball
     * class sport_17 = Volleyball
     * class sport_13 = Women's Basketball
     * class sport_19 = Women's XC
     * class sport_21 = Women's Golf
     * class sport_14 = Women's Lacrosse
     * class sport_16 = Women's Soccer
     * class sport_23 = Women's Swimming
     * class sport_25 = Women's Tennis
     * class sport_27 = Women's Track and Field
     * class sport_28 = Wrestling
     */
    }

    private fun standingsParser(sport: String): (Element) -> Standing = when (sport) {
        "Men's Soccer", "Women's Soccer" -> this::parseSoccerStanding
        else -> this::parseNormalStanding
    }

    // Convert the given string sport into a url sport path
    // Called in generating standings tables
    fun sportToPath(sport: String): String {
        logger.trace("Getting URL path for \"{}\"", sport)

        when (sport) {
            "Baseball" -> return "baseball"
            "Field Hockey" -> return "fhockey"
            "Men's Basketball" -> return "mbball"
            "Men's Soccer" -> return "msoc"
            "Softball" -> return "softball"
            "Women's Basketball" -> return "wbball"
            "Women's Soccer" -> return "wsoc"
            "Volleyball" -> return "wvball"
            else -> return ""
        }
    }

    // In general, the standings tables look like
    // Hofstra | 0 - 12 | 0.000 | 5 - 25 | 0.2000
    // So this just grabs the correct elements.
    // Parse a generic standing table row into a Standing object
    private fun parseNormalStanding(element: Element): Standing {
        return Standing(
            element.child(0).text(), // School
            element.child(1).text(), // Conference Results
            element.child(3).text()) // Overall Results
    }

    // In contrast, soccer standings look like
    // Hofstra | 0-12 | 0.000 | 0 | 5 - 25 | 0.200 | 15
    // Where the extra elements are points. So this just corrects for the
    // change.
    // Parse a soccer standing table row into a Standing object
    private fun parseSoccerStanding(element: Element): Standing {
        return Standing(
            element.child(0).text(), // School
            element.child(1).text(), // Conference Results
            element.child(4).text()) // Overall results
    }

    // Parse the table row in the document into a Match
    private fun parseMatch(element: Element): Match {
        val (opponent, northeasternIndex, opponentIndex) =
            if (element.child(1).text() == "Northeastern") {
                Triple(element.child(4).text(), 1, 4)
            } else {
                Triple(element.child(1).text(), 4, 1)
            }

        val result = if (element.child(northeasternIndex).hasClass("won")) {
            val winningScore = element.child(2).text().trim()
            val losingScore = element.child(5).text().trim()
            "W $winningScore - $losingScore"
        } else if (element.child(opponentIndex).hasClass("won")) {
            val winningScore = element.child(2).text().trim()
            val losingScore = element.child(5).text().trim()
            "L $winningScore - $losingScore"
        } else if (element.child(opponentIndex + 1).text().trim { it <= ' ' }.isEmpty()) {
            ""
        } else {
            val tiedScore = element.child(opponentIndex + 1).text().trim()
            "$tiedScore - $tiedScore"
        }

        val date = this.getDate(element)

        return Match(date, opponent, result)
    }

    // Look up through the table until it hits a row that contains the date
    private fun getDate(element: Element?): String {
        var loopingElement = element
        while (loopingElement != null && !loopingElement.hasAttr("data-date")) {
            loopingElement = loopingElement.previousElementSibling()
        }

        return loopingElement?.attr("data-date") ?: ""
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
