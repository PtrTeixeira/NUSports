/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.dao

import com.github.ptrteixeira.nusports.model.Standing

interface IStandingsDao {
    // TODO(pteixeira) introduce enum here
    suspend fun get(sport: String): List<Standing>
    fun clear(sport: String)
}