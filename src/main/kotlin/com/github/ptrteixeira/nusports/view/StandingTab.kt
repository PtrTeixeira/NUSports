package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.StandingK
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.column
import tornadofx.tableview

class StandingTab(private val binding: ObservableList<StandingK>) : AbstractTab<StandingK>() {
    private val tableContents = FXCollections.observableArrayList<StandingK>()

    init {
        binding.addListener(ListChangeListener { tableContents.setAll(binding) })
    }

    override val root = place {
        tableview<StandingK> {
            items = tableContents

            column("Team", StandingK::teamName)
            column("Conference", StandingK::conference)
            column("Overall", StandingK::overall)
        }
    }

    override fun populate(tableData: List<StandingK>) {
        tableContents.setAll(tableData)
    }
}
