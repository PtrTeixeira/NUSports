/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.view

import com.github.ptrteixeira.nusports.model.ConnectionError
import com.github.ptrteixeira.nusports.model.InteractionEvent
import com.github.ptrteixeira.nusports.model.ReloadEvent
import com.github.ptrteixeira.nusports.model.SportChangeEvent
import com.github.ptrteixeira.nusports.model.UpdateEvent
import com.github.ptrteixeira.nusports.presenter.ViewState
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ComboBox
import javafx.scene.control.TabPane
import javafx.scene.paint.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.View
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.center
import tornadofx.combobox
import tornadofx.left
import tornadofx.onChange
import tornadofx.paddingLeft
import tornadofx.progressbar
import tornadofx.tab
import tornadofx.tabpane
import tornadofx.text
import tornadofx.vbox
import tornadofx.visibleWhen

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class Body : View(), CoroutineScope {
    override val coroutineContext = Dispatchers.JavaFx
    override val refreshable = SimpleBooleanProperty(true)
    override val savable = SimpleBooleanProperty(false)
    override val deletable = SimpleBooleanProperty(false)

    private val loading = SimpleBooleanProperty(false)

    private val scheduleTable = ScheduleTable()
    private val standingsTable = StandingsTable()

    private val errorText = SimpleStringProperty()
    private val currentSelection = SimpleStringProperty()
    private val viewState: ViewState by di()
    private val sports = viewState.selectableSports

    private val sportChangeEventChannel = actor<SportChangeEvent>(capacity = Channel.CONFLATED) {
        for (sportChangeEvent in channel) {
            if (channel.isEmpty) {
                loading.set(false)
            }

            val viewUpdate = viewState.getViewUpdate(sportChangeEvent)
            scheduleTable.send(viewUpdate)
            standingsTable.send(viewUpdate)
            updateErrorText(viewUpdate)
        }
    }

    override val root = borderpane {
        left {
            vbox {
                alignment = javafx.geometry.Pos.TOP_CENTER

                combobox<String>(currentSelection, sports) {
                    onChange {
                        sportChangeEventChannel.offer(InteractionEvent(it))
                        loading.set(true)
                    }
                    initialValue(sports[0])
                }

                text(errorText) {
                    fill = Color.RED
                    styleClass += "error"
                }
            }
        }

        center {
            tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                paddingLeft = 10

                tab("Standings") {
                    add(standingsTable)
                }
                tab("Schedule") {
                    add(scheduleTable)
                }
            }
        }

        bottom {
            progressbar {
                visibleWhen(loading)
            }
        }
    }

    override fun onRefresh() {
        sportChangeEventChannel.offer(ReloadEvent(currentSelection.value))
    }

    private fun updateErrorText(updateEvent: UpdateEvent) {
        when (updateEvent) {
            is ConnectionError -> errorText.set(updateEvent.errorText)
            else -> errorText.set("")
        }
    }

    private fun <T> ComboBox<T>.onChange(action: (T) -> Unit) {
        this.valueProperty().onChange {
            if (it != null) {
                action(it)
            }
        }
    }

    private fun <T> ComboBox<T>.initialValue(value: T) {
        this.valueProperty().set(value)
    }
}
