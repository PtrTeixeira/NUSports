package com.github.ptrteixeira.nusports.view

import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import tornadofx.View
import tornadofx.borderpane
import tornadofx.center
import tornadofx.combobox
import tornadofx.left
import tornadofx.singleAssign
import tornadofx.text
import tornadofx.vbox

abstract class KAbstractTab<in TableData> : View() {
    private val sports = FXCollections.observableArrayList<String>()

    private var errorText : Text by singleAssign()
    private var sportSelector: ComboBox<String> by singleAssign()

    protected fun place(table: BorderPane.() -> Unit): BorderPane {
        return borderpane {
            left {
                vbox {
                    alignment = Pos.TOP_CENTER
                    sportSelector = combobox<String> {
                        styleClass += "sportSelector"
                        items = sports
                    }

                    errorText = text("") {
                        fill = Color.RED
                        styleClass += "error"
                    }
                }
            }

            center(table)
        }
    }

    abstract fun populate(tableData: List<TableData>)

    fun setErrorText(message : String) {
        this.errorText.text = message
    }

    fun clearErrorText() = setErrorText("")

    fun setSportSelections(sports : List<String>) {
        this.sports.setAll(sports)
        this.sportSelector.selectionModel.select(0)
    }

    fun setSelectedSport(sport : String) {
        this.sportSelector.selectionModel.select(sport)
    }

    fun setSportSelectionCallback(changeListener : ChangeListener<String>) {
        this.sportSelector.valueProperty().addListener(changeListener)
    }
}
