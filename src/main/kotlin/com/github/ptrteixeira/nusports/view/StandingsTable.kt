/* Released under the MIT license, $YEAR */

package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.Standing
import javafx.collections.ObservableList
import tornadofx.View
import tornadofx.column
import tornadofx.tableview

class StandingsTable(contents: ObservableList<Standing>) : View() {
    override val root = tableview(contents) {
        column("Team", Standing::teamName)
        column("Conference", Standing::conference)
        column("Overall", Standing::overall)
    }
}
