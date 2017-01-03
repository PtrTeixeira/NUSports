package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.MatchK
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.column
import tornadofx.tableview

class ScheduleTab(private val binding : ObservableList<MatchK>) : AbstractTab<MatchK>() {
    private var tableContents = FXCollections.observableArrayList<MatchK>()

    init {
        binding.addListener(ListChangeListener {
            tableContents.setAll(binding)
        })
    }

    override val root = place {
        tableview<MatchK> {
            items = tableContents

            column("Date", MatchK::date)
            column("Opponent", MatchK::opponent)
            column("Result", MatchK::result)
        }
    }

    override fun populate(tableData : List<MatchK>) {
        this.tableContents.setAll(tableData)
    }

    data class Match(val date : String, val opponent : String, val result : String) {
        constructor(match: com.github.ptrteixeira.nusports.model.Match) :
        this(match.date, match.opponent, match.result)
    }
}
