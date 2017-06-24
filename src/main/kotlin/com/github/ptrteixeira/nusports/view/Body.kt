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
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.View
import tornadofx.observable
import tornadofx.onChange
import tornadofx.tab
import tornadofx.tabpane

class Body : View() {
    private val controller: MainController by di()
    private val sports = controller.getSelectableSports().observable()
    private val currentSelection = SimpleStringProperty("Baseball")
    private val displayType = SimpleObjectProperty(SCHEDULE)
    private val errorText = SimpleStringProperty("")

    private val scheduleTab = ScheduleTab(sports, controller.displayedSchedule, currentSelection, errorText)
    private val standingsTab = StandingsTab(sports, controller.displayedStandings, currentSelection, errorText)

    override val refreshable = SimpleBooleanProperty(true)
    override val savable = SimpleBooleanProperty(false)
    override val deletable = SimpleBooleanProperty(false)

    override val root: Parent = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        tab("Schedule", scheduleTab.root)
        tab("Standings", standingsTab.root)

        onTabChange { newTab ->
            displayType.set(getDisplayType(newTab))
        }
    }

    init {
        currentSelection.onChange { newSelection ->
            newSelection?.let { controller.lookup(displayType.value, it) }
        }
        displayType.onChange { newDisplayType ->
            newDisplayType?.let { controller.lookup(it, currentSelection.value) }
        }

        runAsync { controller.lookup(displayType.value, currentSelection.value) }
    }

    override fun onRefresh() {
        runAsync { controller.lookup(displayType.value, currentSelection.value, clearOnFail = false) }
    }

    private fun getDisplayType(tab: Tab) = when (tab.text) {
        "Schedule" -> SCHEDULE
        "Standings" -> STANDINGS
        else -> throw IllegalStateException("Invalid tab title ${tab.text}")
    }

    private fun TabPane.onTabChange(op: (Tab) -> Unit): Unit {
        this.selectionModel.selectedItemProperty().onChange {
            it?.let(op)
        }
    }
}
