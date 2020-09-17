/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.dao

import com.github.ptrteixeira.nusports.model.ConnectionFailureException
import com.github.ptrteixeira.nusports.model.Match
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.time.Clock
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ScheduleDao internal constructor(
    private val scheduleCache: MutableMap<String, List<Match>>,
    private val clock: Clock,
    private val caaService: CaaService
) : IScheduleDao {
    override suspend fun get(sport: String): List<Match> {
        val cached = scheduleCache[sport]
        if (cached != null) {
            return cached
        }

        logger.debug("Schedule for \"{}\" not found in cache; connecting to external source", sport)
        val start = ZonedDateTime.now(clock).minus(2, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_OFFSET_DATE)
        val end = ZonedDateTime.now(clock).plus(2, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_OFFSET_DATE)
        val sportId = sportToIdNumber(sport)

        val schedule = try {
            caaService
                .getSchedule(start, end, sportId, NORTHEASTERN_ID_NUMBER)
                .read()
        } catch (iex: IOException) {
            logger.trace("Connection failure getting schedule data", iex)
            throw ConnectionFailureException("Failed to connect to the internet.")
        }

        if (schedule == null) {
            return emptyList()
        }

        return schedule.map {
            val results = it.result?.format() ?: ""
            Match(it.date, it.opponent.title, results)
        }.apply {
            scheduleCache[sport] = this
        }
    }

    override fun clear(sport: String) {
        scheduleCache.remove(sport)
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

    companion object {
        private const val NORTHEASTERN_ID_NUMBER = "3"
        private val logger = LogManager.getLogger()
    }
}
