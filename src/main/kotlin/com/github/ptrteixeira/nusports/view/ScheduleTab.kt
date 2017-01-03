package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.Match
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.column
import tornadofx.tableview

class ScheduleTab(private val binding : ObservableList<Match>) : AbstractTab<Match>() {
    private var tableContents = FXCollections.observableArrayList<Match>()

    init {
        binding.addListener(ListChangeListener {
            tableContents.setAll(binding)
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

    override fun populate(tableData : List<Match>) {
        this.tableContents.setAll(tableData)
    }
}
