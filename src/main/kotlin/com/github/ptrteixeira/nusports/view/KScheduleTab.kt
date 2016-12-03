package com.github.ptrteixeira.nusports.view

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.column
import tornadofx.tableview

class KScheduleTab(private val binding : ObservableList<com.github.ptrteixeira.nusports.model.Match>) : KAbstractTab<com.github.ptrteixeira.nusports.model.Match>() {
    private var tableContents = FXCollections.observableArrayList<Match>()

    init {
        binding.addListener(ListChangeListener { change ->
            tableContents.clear()
            binding.map {
                Match(it.date, it.opponent, it.result)
            }.toCollection(tableContents)
        })
    }

    override val root = place {
        tableview<Match> {
            items = tableContents

            column("Date", Match::date)
            column("Opponent", Match::opponent)
            column("Result", Match::result)
        }
    }

    override fun populate(tableData : List<com.github.ptrteixeira.nusports.model.Match>) {
        this.tableContents.clear()
        tableData.map {
            Match(it.date, it.opponent, it.result)
        }.toCollection(this.tableContents)
    }

    data class Match(val date : String, val opponent : String, val result : String) {
        constructor(match: com.github.ptrteixeira.nusports.model.Match) :
        this(match.date, match.opponent, match.result)
    }
}
