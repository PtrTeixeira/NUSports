/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

import com.github.ptrteixeira.nusports.dao.IScheduleDao
import com.github.ptrteixeira.nusports.dao.IStandingsDao
import org.apache.logging.log4j.LogManager

/**
 * Scrape the CAA site, in particular, to load information relevant to Northeastern.
 *
 * It is extremely tightly tied to the actual structure of the CAA site, but I couldn't find a
 * REST endpoint or anything similar that would allow me to trivially extract the information that I
 * needed. So it just scrapes straight off of HTML, which works until the CAA changes how their site
 * is laid out again.

 * @author Peter
 */
internal class NuWebScraper(
    private val scheduleDao: IScheduleDao,
    private val standingsDao: IStandingsDao
) : WebScraper {
    override suspend fun getStandings(sport: String): List<Standing> {
        return standingsDao.get(sport)
    }

    override suspend fun getSchedule(sport: String): List<Match> {
        return scheduleDao.get(sport)
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
        logger.info("Clearing the cache for \"{}\"", sport)
        scheduleDao.clear(sport)
        standingsDao.clear(sport)
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
