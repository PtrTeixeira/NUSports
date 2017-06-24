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

import com.github.ptrteixeira.nusports.presenter.MainController
import com.github.ptrteixeira.nusports.view.DisplayType.SCHEDULE
import com.github.ptrteixeira.nusports.view.DisplayType.STANDINGS
import javafx.beans.value.ChangeListener
import javafx.geometry.Pos
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE
import tornadofx.View
import tornadofx.add
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.button
import tornadofx.center
import tornadofx.hbox
import tornadofx.tab
import tornadofx.tabpane

class MainView(private val controller : MainController) : View() {
    private val scheduleTab = ScheduleTab(controller.displayedSchedule)
    private val standingsTab = StandingTab(controller.displayedStandings)

    private var displayType: DisplayType = SCHEDULE
    private var currentSelection = "Baseball"

    override val root = borderpane {
        minWidth = 600.toDouble()
        minHeight = 600.toDouble()

        center {
            tabpane {
                tabClosingPolicy = UNAVAILABLE

                setTabChangeListener(changeTabs())

                tab("Schedule") {
                    addRoot(scheduleTab) {
                        setSportSelections(controller.getSelectableSports())
                        setSportSelectionCallback(changeSport())
                    }
                }
                tab("Standings") {
                    addRoot(standingsTab) {
                        setSportSelections(controller.getSelectableSports())
                        setSportSelectionCallback(changeSport())
                    }
                }
            }
        }

        bottom {
            hbox {
                alignment = Pos.CENTER_RIGHT

                button("Reload") {
                    setOnMouseClicked {
                        controller
                            .lookup(displayType, currentSelection, true)
                    }
                }
            }
        }
    }

    private fun changeTabs() : ChangeListener<Tab> {
        return ChangeListener { _, _, newValue ->
            displayType = when (newValue.text) {
                "Schedule" -> SCHEDULE
                "Standings" -> STANDINGS
                else -> throw IllegalStateException("Invalid tab title")
            }

            controller.lookup(displayType, currentSelection)
        }
    }

    private fun changeSport(): ChangeListener<String> {
        return ChangeListener { _, _, newValue ->
            currentSelection = newValue

            controller.lookup(displayType, currentSelection, clearOnFail = true)
            scheduleTab.setSelectedSport(currentSelection)
            standingsTab.setSelectedSport(currentSelection)
        }
    }
}

private fun TabPane.setTabChangeListener(op: ChangeListener<in Tab>) {
    this.selectionModel
        .selectedItemProperty()
        .addListener(op)
}

private fun Tab.addRoot(abstractViewTab: AbstractTab, op: AbstractTab.() -> Unit) {
    abstractViewTab.apply(op)

    this.add(abstractViewTab.root)
}
