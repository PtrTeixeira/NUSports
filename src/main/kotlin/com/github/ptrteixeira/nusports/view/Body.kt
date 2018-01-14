/* Released under the MIT license, $YEAR */

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
    private val loading = viewState.isLoading

    private val standingsTable = StandingsTable(viewState.displayedStandings)
    private val scheduleTable = ScheduleTable(viewState.displayedSchedule)
    private val standingsTab = BodyTab(sports, standingsTable, currentSelection, errorText, loading)
    private val scheduleTab = BodyTab(sports, scheduleTable, currentSelection, errorText, loading)

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
