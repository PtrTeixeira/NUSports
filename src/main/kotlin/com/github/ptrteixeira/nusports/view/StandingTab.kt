package com.github.ptrteixeira.nusports.view

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.column
import tornadofx.tableview

class StandingTab(private val binding
                   : ObservableList<com.github.ptrteixeira.nusports.model.Standing>)
: AbstractTab<com.github.ptrteixeira.nusports.model.Standing>() {
    private val tableContents = FXCollections.observableArrayList<Standing>()

    init {
        binding.addListener(ListChangeListener { change ->
            tableContents.clear()
            binding.map {
                Standing(it.teamName, it.conference, it.overall)
            }.toCollection(tableContents)
        })
    }

    override val root = place {
        tableview<Standing> {
            items = tableContents

            column("Team", Standing::team)
            column("Conference", Standing::conference)
            column("Overall", Standing::overall)
        }
    }

    override fun populate(tableData: List<com.github.ptrteixeira.nusports.model.Standing>) {
        this.tableContents.clear()
        tableData.map {
            Standing(it.teamName, it.conference, it.overall)
        }.toCollection(tableContents)
    }

    private data class Standing(val team : String, val conference : String, val overall : String)
}
