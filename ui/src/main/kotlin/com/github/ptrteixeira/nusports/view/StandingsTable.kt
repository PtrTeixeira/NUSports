/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.ConnectionError
import com.github.ptrteixeira.nusports.model.Standing
import com.github.ptrteixeira.nusports.model.UpdateEvent
import com.github.ptrteixeira.nusports.model.VisibleSport
import javafx.collections.FXCollections
import tornadofx.View
import tornadofx.readonlyColumn
import tornadofx.tableview

class StandingsTable() : View() {
    private val contents = FXCollections.observableArrayList<Standing>()

    override val root = tableview(contents) {
        readonlyColumn("Team", Standing::teamName)
        readonlyColumn("Conference", Standing::conference)
        readonlyColumn("Overall", Standing::overall)
    }

    fun send(updateEvent: UpdateEvent) {
        when (updateEvent) {
            is VisibleSport -> contents.setAll(updateEvent.standings)
            is ConnectionError -> contents.clear()
        }
    }
}
