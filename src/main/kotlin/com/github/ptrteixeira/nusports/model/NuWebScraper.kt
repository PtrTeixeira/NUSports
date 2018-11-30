/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.logging.log4j.LogManager
import org.jsoup.nodes.Element
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import java.time.Clock
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Objects
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Scrape the CAA site, in particular, to load information relevant to Northeastern.
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
    private val documentSource: DocumentSource
) : WebScraper {
    // TODO Inject
    private val objectMapper = ObjectMapper().apply {
        registerModule(KotlinModule())
        enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
    }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://caasports.com")
        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
        .build()
    private val caaService = retrofit.create(CaaService::class.java)
    private val clock = Clock.systemDefaultZone()

    @Throws(ConnectionFailureException::class)
    override suspend fun getStandings(sport: String): List<Standing> {
        val result = standingsCache[sport]

        return if (result == null) {
            logger.debug("Standings for {} not found in cache. Connecting to external source", sport)
            val computed = loadStandings(sport)
            standingsCache[sport] = computed
            computed
        } else {
            result
        }
    }

    @Throws(ConnectionFailureException::class)
    override suspend fun getSchedule(sport: String): List<Match> {
        val result = scheduleCache[sport]

        return if (result == null) {
            logger.debug("Schedule for {} not found in cache. Connecting to external source", sport)
            val computed = loadSchedule(sport)
            scheduleCache[sport] = computed
            computed
        } else {
            result
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
            val queryPath = "https://caasports.com/standings.aspx?path=${sportToPath(sport)}"
            logger.debug("Making query to path {}", queryPath)
            val doc = documentSource.load(queryPath)

            val rows = doc.getElementsByClass("sidearm-standings-table") // list of <table>
                ?.first() // <table>
                ?.getElementsByTag("tbody") // list of <tbody>
                ?.first() // <tbody>
                ?.children() // list of <tr>

            return rows
                ?.map(standingsParser(sport))
                ?: listOf()
        } catch (iex: IOException) {
            logger.warn("Connection failure getting standings data", iex)
            throw ConnectionFailureException("Failed to connect to the internet.")
        }
    }

    @Throws(ConnectionFailureException::class)
    private suspend fun loadSchedule(sport: String): List<Match> {
        try {
            logger.debug("Schedule for \"{}\" not found in cache; connecting to external source", sport)
            val start = ZonedDateTime.now(clock).minus(2, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_OFFSET_DATE)
            val end = ZonedDateTime.now(clock).plus(2, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_OFFSET_DATE)
            val sportId = sportToIdNumber(sport)
            val schoolId = NORTHEASTERN_ID_NUMBER

            val schedule = caaService
                .getSchedule(start, end, sportId, schoolId)
                .read()

            return if (schedule == null) {
                // TODO should probably do something else here
                emptyList()
            } else {
                schedule.map {
                    val results = it.result?.format() ?: ""
                    Match(it.date, it.opponent.title, results)
                }
            }
        } catch (iex: IOException) {
            logger.trace("Connection failure getting schedule data", iex)
            throw ConnectionFailureException("Failed to connect to the internet.")
        }
    }

    // Convert the given string sport into an HTML class
    // Used in extracting data from the calendar
    fun sportToClass(sport: String): String {
        logger.debug("Finding CSS class for \"{}\"", sport)
        return "sport_${sportToIdNumber(sport)}"
    }

    // Convert the given string sport into an HTML class
    // Used in extracting data from the calendar
    private fun sportToIdNumber(sport: String): String {
        logger.debug("Finding CSS class for \"{}\"", sport)

        return when (sport) {
            "Baseball" -> "1"
            "Field Hockey" -> "3"
            "Men's Basketball" -> "6"
            "Men's Soccer" -> "8"
            "Softball" -> "9"
            "Women's Basketball" -> "13"
            "Women's Soccer" -> "16"
            "Volleyball" -> "17"
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

        return when (sport) {
            "Baseball" -> "baseball"
            "Field Hockey" -> "fhockey"
            "Men's Basketball" -> "mbball"
            "Men's Soccer" -> "msoc"
            "Softball" -> "softball"
            "Women's Basketball" -> "wbball"
            "Women's Soccer" -> "wsoc"
            "Volleyball" -> "wvball"
            else -> ""
        }
    }

    // In general, the standings tables look like
    // Hofstra | Hofstra | 0 - 12 | 0.000 | 5 - 25 | 0.2000
    // So this just grabs the correct elements.
    // Parse a generic standing table row into a Standing object
    private fun parseNormalStanding(element: Element): Standing {
        return Standing(
            element.child(0).text(), // School
            element.child(3).text(), // Conference Results
            element.child(4).text() // Overall results
        )
    }

    // In contrast, soccer standings look like
    // Hofstra | 0-12 | 0.000 | 0 | 5 - 25 | 0.200 | 15
    // Where the extra elements are points. So this just corrects for the
    // change.
    // Parse a soccer standing table row into a Standing object
    private fun parseSoccerStanding(element: Element): Standing {
        return Standing(
            element.child(0).text(), // School
            element.child(2).text(), // Conference Results
            element.child(4).text()
        ) // Overall results
    }

    private suspend fun <T> Call<T>.read(): T? {
        return suspendCoroutine {
            this.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    it.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    it.resume(response.body())
                }
            })
        }
    }

    companion object {
        private const val NORTHEASTERN_ID_NUMBER = "3"
        private val logger = LogManager.getLogger()
    }
}
