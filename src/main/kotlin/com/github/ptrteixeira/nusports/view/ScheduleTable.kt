/* Released under the MIT license, $YEAR */

package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.Match
import javafx.collections.ObservableList
import tornadofx.View
import tornadofx.column
import tornadofx.tableview

class ScheduleTable(contents: ObservableList<Match>) : View() {
    override val root = tableview(contents) {
        column("Date", Match::date)
        column("Opponent", Match::opponent)
        column("Result", Match::result)
    }
}
