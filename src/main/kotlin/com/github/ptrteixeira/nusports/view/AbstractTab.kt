/*
 * Copyright (c) 2017 Peter Teixeira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

abstract class AbstractTab : View() {
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
