/* Released under the MIT license, 2019 */

package com.github.ptrteixeira.nusports.model

import org.apache.logging.log4j.LogManager
import java.lang.IllegalStateException

internal class FullWebScraper(webScrapers: Iterable<WebScraper>) : WebScraper {
    private val backingSportsMap = mutableMapOf<String, WebScraper>()

    init {
        var discovered = 0
        for (webScraper in webScrapers) {
            discovered += 1
            for (sport in webScraper.selectableSports) {
                if (backingSportsMap.containsKey(sport)) {
                    throw IllegalStateException("Multiple web scrapers discovered claiming to support the same sport")
                } else {
                    backingSportsMap[sport] = webScraper
                }
            }
        }
        logger.info("Discovered {} web scrapers during loading sequence", discovered)

        if (backingSportsMap.isEmpty()) {
            throw IllegalStateException("No supported sports discovered")
        }
    }

    override val selectableSports: List<String>
        get() = backingSportsMap.keys.toList()

    override suspend fun getStandings(sport: String): List<Standing> {
        val webScraperOrNull = backingSportsMap[sport]
        if (webScraperOrNull == null) {
            throw IllegalArgumentException("Given sport is not supported")
        } else {
            return webScraperOrNull.getStandings(sport)
        }
    }

    override suspend fun getSchedule(sport: String): List<Match> {
        val webScraperOrNull = backingSportsMap[sport]
        if (webScraperOrNull == null) {
            throw IllegalArgumentException("Given sport is not supported")
        } else {
            return webScraperOrNull.getSchedule(sport)
        }
    }

    override fun clearCache(sport: String) {
        val webScraperOrNull = backingSportsMap[sport]
        if (webScraperOrNull == null) {
            throw IllegalArgumentException("Given sport is not supported")
        } else {
            webScraperOrNull.clearCache(sport)
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}