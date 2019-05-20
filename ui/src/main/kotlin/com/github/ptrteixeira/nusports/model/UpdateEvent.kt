/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

sealed class UpdateEvent
data class VisibleSport(val matches: List<Match>, val standings: List<Standing>) : UpdateEvent()
data class ConnectionError(val errorText: String, val throwable: Throwable? = null) : UpdateEvent()