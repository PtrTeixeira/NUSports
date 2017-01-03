package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.presenter.MainController
import com.github.ptrteixeira.nusports.view.DisplayType.SCHEDULE
import com.github.ptrteixeira.nusports.view.DisplayType.STANDINGS
import javafx.beans.value.ChangeListener
import javafx.geometry.Pos
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE
import org.apache.logging.log4j.LogManager
import tornadofx.*

class MainView(private val controller : MainController) : View() {
    private val logger = LogManager.getLogger()

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
        return ChangeListener { observable, oldValue, newValue ->
            displayType = when (newValue.text) {
                "Schedule" -> SCHEDULE
                "Standings" -> STANDINGS
                else -> throw IllegalStateException("Invalid tab title")
            }

            controller.lookup(displayType, currentSelection)
        }
    }

    private fun changeSport(): ChangeListener<String> {
        return ChangeListener { observable, oldValue, newValue ->
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

private fun <T> Tab.addRoot(abstractViewTab: AbstractTab<T>, op: AbstractTab<T>.() -> Unit) {
    abstractViewTab.apply(op)

    this.add(abstractViewTab.root)
}
