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
package com.github.ptrteixeira.nusports.presenter

import com.github.ptrteixeira.nusports.model.ConnectionFailureException
import com.github.ptrteixeira.nusports.model.Match
import com.github.ptrteixeira.nusports.model.Standing
import com.github.ptrteixeira.nusports.model.WebScraper
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import tornadofx.ViewModel
import tornadofx.onChange

class ViewState(private val webScraper: WebScraper) : ViewModel() {
    val displayedSchedule: ObservableList<Match> = FXCollections.observableArrayList()
    val displayedStandings: ObservableList<Standing> = FXCollections.observableArrayList()
    val errorText: SimpleStringProperty = SimpleStringProperty("")
    val selectableSports: List<String> = webScraper.selectableSports

    val selectedSport: SimpleStringProperty = SimpleStringProperty(selectableSports[0])

    init {
        selectedSport.onChange { newSelection ->
            newSelection?.let { update(it) }
        }

        update(selectedSport.value)
    }

    private fun update(selectedSport: String) {
        launch(CommonPool) {
            blockingUpdate(selectedSport)
        }
    }

    suspend fun blockingUpdate(selectedSport: String) {
        try {
            val schedule = async(CommonPool) { webScraper.getSchedule(selectedSport) }
            val standings = async(CommonPool) { webScraper.getStandings(selectedSport) }
            displayedSchedule.setAll(schedule.await())
            displayedStandings.setAll(standings.await())
        } catch (exn: ConnectionFailureException) {
            errorText.value = exn.message
        }
    }

    fun reload() {
        webScraper.clearCache(selectedSport.value)
        update(selectedSport.value)
    }
}
