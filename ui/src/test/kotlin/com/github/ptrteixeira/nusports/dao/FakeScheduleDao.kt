/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.dao

import com.github.ptrteixeira.nusports.model.Match

class FakeScheduleDao(private val doOnGet: suspend () -> List<Match>) : IScheduleDao {
    override suspend fun get(sport: String): List<Match> {
        return doOnGet()
    }

    override fun clear(sport: String) {
        /* Don't plan on implementing this */
    }
}