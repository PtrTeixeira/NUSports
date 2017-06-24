package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.Match
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.scene.paint.Color
import tornadofx.View
import tornadofx.borderpane
import tornadofx.center
import tornadofx.column
import tornadofx.combobox
import tornadofx.left
import tornadofx.tableview
import tornadofx.text
import tornadofx.vbox

class ScheduleTab(
    private val sports: ObservableList<String>,
    private val tableContents: ObservableList<Match>,
    selectedItem: StringProperty,
    errorText: StringProperty
) : View() {

    override val root = borderpane {
        left {
            vbox {
                alignment = javafx.geometry.Pos.TOP_CENTER

                combobox<String>(selectedItem, sports)

                text(errorText) {
                    fill = Color.RED
                    styleClass += "error"
                }
            }
        }

        center {
            tableview(tableContents) {
                column("Date", Match::date)
                column("Opponent", Match::opponent)
                column("Result", Match::result)
            }
        }
    }
}
