package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.Match
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.scene.paint.Color
import tornadofx.View
import tornadofx.bind
import tornadofx.borderpane
import tornadofx.center
import tornadofx.column
import tornadofx.combobox
import tornadofx.left
import tornadofx.tableview
import tornadofx.text
import tornadofx.vbox

class ScheduleTab2(
    private val sports: ObservableList<String>,
    private val tableContents: ObservableList<Match>,
    selectedItem: StringProperty,
    errorText: StringProperty
) : View() {

    override val root = borderpane {
        left {
            vbox {
                alignment = javafx.geometry.Pos.TOP_CENTER
                combobox<String> {
                    items = sports
                    valueProperty()
                        .bindBidirectional(selectedItem)
                }

                text("") {
                    bind(errorText)
                    fill = javafx.scene.paint.Color.RED
                    styleClass += "error"
                }
            }
        }

        center {
            tableview(tableContents) {
                column("Date", com.github.ptrteixeira.nusports.model.Match::date)
                column("Opponent", com.github.ptrteixeira.nusports.model.Match::opponent)
                column("Result", com.github.ptrteixeira.nusports.model.Match::result)
            }
        }
    }
}
