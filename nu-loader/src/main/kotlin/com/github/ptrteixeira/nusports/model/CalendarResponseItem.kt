/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

enum class EventStatus {
    CANCELLED {
        override fun toString() = "C"
    },
    FINISHED {
        override fun toString() = "O"
    },
    UPCOMING {
        override fun toString() = "A"
    },
    POSTPONED {
        // Just guessing here
        override fun toString() = "P"
    }
}

enum class ResultStatus {
    WIN {
        override fun toString() = "W"
    },
    LOSS {
        override fun toString() = "L"
    },
    TIE {
        override fun toString() = "T"
    },
    CANCELLED_DURING_PLAY {
        override fun toString() = "N"
    }
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