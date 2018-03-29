package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.Standing
import javafx.collections.ObservableList
import tornadofx.View
import tornadofx.readonlyColumn
import tornadofx.tableview

class StandingsTable(contents: ObservableList<Standing>) : View() {
    override val root = tableview(contents) {
        readonlyColumn("Team", Standing::teamName)
        readonlyColumn("Conference", Standing::conference)
        readonlyColumn("Overall", Standing::overall)
    }
}
