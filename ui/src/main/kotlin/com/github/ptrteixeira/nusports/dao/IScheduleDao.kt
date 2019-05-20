/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.dao

import com.github.ptrteixeira.nusports.model.Match

interface IScheduleDao {
    suspend fun get(sport: String): List<Match>
    fun clear(sport: String)
}