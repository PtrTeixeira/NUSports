/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.dao

import com.github.ptrteixeira.nusports.model.ConnectionFailureException
import com.github.ptrteixeira.nusports.model.Standing
import org.apache.logging.log4j.LogManager
import org.jsoup.nodes.Element
import java.io.IOException

class StandingsDao internal constructor(
    private val standingsCache: MutableMap<String, List<Standing>>,
    private val documentSource: DocumentSource
) : IStandingsDao {
    override suspend fun get(sport: String): List<Standing> {
        val cached = standingsCache[sport]
        if (cached != null) {
            return cached
        }

        val queryPath = "https://caasports.com/standings.aspx?path=${sportToPath(sport)}"
        logger.debug("Making query to path {}", queryPath)

        val rows = try {
            logger.debug("Making query to path {}", queryPath)
            val doc = documentSource.load(queryPath)

            doc.getElementsByClass("sidearm-standings-table") // list of <table>
                ?.first() // <table>
                ?.getElementsByTag("tbody") // list of <tbody>
                ?.first() // <tbody>
                ?.children() // list of <tr>
        } catch (iex: IOException) {
            logger.warn("Connection failure getting standings data", iex)
            throw ConnectionFailureException("Failed to connect to the internet.")
        }

        return rows
            ?.map(standingsParser(sport))
            ?.apply { standingsCache[sport] = this }
            ?: listOf()
    }

    override fun clear(sport: String) {
        standingsCache.remove(sport)
    }

    private fun standingsParser(sport: String): (Element) -> Standing = when (sport) {
        "Men's Soccer", "Women's Soccer" -> this::parseSoccerStanding
        else -> this::parseNormalStanding
    }

    // Convert the given string sport into a url sport path
    // Called in generating standings tables
    private fun sportToPath(sport: String): String {
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

    companion object {
        private val logger = LogManager.getLogger()
    }
}
