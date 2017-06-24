package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.Standing
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

class StandingsTab(
    private val sports: ObservableList<String>,
    private val tableContents: ObservableList<Standing>,
    selectedItem: StringProperty,
    errorText: StringProperty
) : View() {

    override val root = borderpane {
        left {
            vbox {
                alignment = javafx.geometry.Pos.TOP_CENTER

                combobox<String>(selectedItem, sports)

                text(errorText) {
                    fill = javafx.scene.paint.Color.RED
                    styleClass += "error"
                }
            }
        }

        center {
            tableview(tableContents) {
                column("Team", Standing::teamName)
                column("Conference", Standing::conference)
                column("Overall", Standing::overall)
            }
        }
    }
}
