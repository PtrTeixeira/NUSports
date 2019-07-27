/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

enum class EventStatus(private val stringValue: String) {
    CANCELLED("C"),
    FINISHED("O"),
    UPCOMING("A"),
    POSTPONED("P");

    override fun toString() = stringValue
}

enum class ResultStatus(private val stringValue: String) {
    WIN("W"),
    LOSS("L"),
    TIE("T"),
    CANCELLED_DURING_PLAY("N");

    override fun toString() = stringValue
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Team(
    val title: String
)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class EventResult(
    val status: ResultStatus?,
    val teamScore: String?,
    val opponentScore: String?
) {
    fun format(): String {
        return if (status == null) {
            ""
        } else {
            "$status $teamScore - $opponentScore"
        }
    }
}

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class CalendarResponseItem(
    val date: String,
    val time: String,
    val opponent: Team,
    val status: EventStatus,
    val result: EventResult?
)