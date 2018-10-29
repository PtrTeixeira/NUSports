/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.presenter

import com.github.ptrteixeira.nusports.model.Match
import com.github.ptrteixeira.nusports.model.Standing
import com.github.ptrteixeira.nusports.model.WebScraper

class MockWebScraper(private val mockImpl: SyncWebScraper) : WebScraper {
    override val selectableSports: List<String>
        get() = mockImpl.selectableSports()

    override suspend fun getStandings(sport: String): List<Standing> = mockImpl.getStandings(sport)

    override suspend fun getSchedule(sport: String): List<Match> = mockImpl.getSchedule(sport)

    override fun clearCache(sport: String) {
        mockImpl.clearCache(sport)
    }
}
