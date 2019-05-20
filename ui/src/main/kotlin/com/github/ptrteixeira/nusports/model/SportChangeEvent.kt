/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

sealed class SportChangeEvent
data class InteractionEvent(val newSport: String) : SportChangeEvent()
data class ReloadEvent(val newSport: String) : SportChangeEvent()