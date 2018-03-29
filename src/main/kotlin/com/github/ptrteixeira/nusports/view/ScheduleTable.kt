package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.Match
import javafx.collections.ObservableList
import tornadofx.View
import tornadofx.readonlyColumn
import tornadofx.tableview

class ScheduleTable(contents: ObservableList<Match>) : View() {
    override val root = tableview(contents) {
        readonlyColumn("Date", Match::date)
        readonlyColumn("Opponent", Match::opponent)
        readonlyColumn("Result", Match::result)
    }
}
