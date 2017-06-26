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

import com.github.ptrteixeira.nusports.presenter.ViewState
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Parent
import javafx.scene.control.TabPane
import tornadofx.View
import tornadofx.tab
import tornadofx.tabpane

class Body : View() {
    private val viewState: ViewState by di()
    private val sports = viewState.selectableSports
    private val currentSelection = viewState.selectedSport
    private val errorText = viewState.errorText

    private val scheduleTab = ScheduleTab(sports, viewState.displayedSchedule, currentSelection, errorText)
    private val standingsTab = StandingsTab(sports, viewState.displayedStandings, currentSelection, errorText)

    override val refreshable = SimpleBooleanProperty(true)
    override val savable = SimpleBooleanProperty(false)
    override val deletable = SimpleBooleanProperty(false)

    override val root: Parent = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        tab("Schedule", scheduleTab.root)
        tab("Standings", standingsTab.root)
    }

    override fun onRefresh() {
        viewState.reload()
    }
}
