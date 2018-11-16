/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.ConnectionError
import com.github.ptrteixeira.nusports.model.Match
import com.github.ptrteixeira.nusports.model.UpdateEvent
import com.github.ptrteixeira.nusports.model.VisibleSport
import javafx.collections.FXCollections
import tornadofx.View
import tornadofx.readonlyColumn
import tornadofx.tableview

class ScheduleTable : View() {
    private val contents = FXCollections.observableArrayList<Match>()

    override val root = tableview(contents) {
        readonlyColumn("Date", Match::date)
        readonlyColumn("Opponent", Match::opponent)
        readonlyColumn("Result", Match::result)
    }

    fun send(updateEvent: UpdateEvent) {
        when (updateEvent) {
            is VisibleSport -> contents.setAll(updateEvent.matches)
            is ConnectionError -> contents.clear()
        }
    }
}
