/* Released under the MIT license, $YEAR */

package com.github.ptrteixeira.nusports.presenter

import com.github.ptrteixeira.nusports.model.ConnectionFailureException
import com.github.ptrteixeira.nusports.model.Match
import com.github.ptrteixeira.nusports.model.Standing

interface SyncWebScraper {
    fun selectableSports(): List<String>
    @Throws(ConnectionFailureException::class)
    fun getStandings(sport: String): List<Standing>
    @Throws(ConnectionFailureException::class)
    fun getSchedule(sport: String): List<Match>
    fun clearCache(sport: String)
}
