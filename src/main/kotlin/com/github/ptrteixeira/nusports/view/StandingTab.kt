package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.Standing
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.column
import tornadofx.tableview

class StandingTab(private val binding: ObservableList<Standing>) : AbstractTab<Standing>() {
    private val tableContents = FXCollections.observableArrayList<Standing>()

    init {
        binding.addListener(ListChangeListener { tableContents.setAll(binding) })
    }

    override val root = place {
        tableview<Standing> {
            items = tableContents

            column("Team", Standing::teamName)
            column("Conference", Standing::conference)
            column("Overall", Standing::overall)
        }
    }

    override fun populate(tableData: List<Standing>) {
        tableContents.setAll(tableData)
    }
}
