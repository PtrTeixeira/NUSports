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
import com.github.ptrteixeira.nusports.view.DisplayType
import com.github.ptrteixeira.nusports.view.DisplayType.SCHEDULE
import com.github.ptrteixeira.nusports.view.DisplayType.STANDINGS
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import org.apache.logging.log4j.LogManager
import tornadofx.Controller
import java.util.concurrent.Executor

class MainController(private val executor: Executor,
                     private val webScraper: WebScraper) : Controller() {
    private val logger = LogManager.getLogger()

    val displayedSchedule: ObservableList<Match>
            = FXCollections.observableArrayList<Match>()
    val displayedStandings: ObservableList<Standing>
            = FXCollections.observableArrayList<Standing>()
    val errorText: SimpleStringProperty = SimpleStringProperty()

    fun lookup(type: DisplayType, currentSelection: String, clearOnFail: Boolean = false) {
        val task = when (type) {
            STANDINGS -> lookupStandings(currentSelection, onFail(type, clearOnFail))
            SCHEDULE -> lookupSchedule(currentSelection, onFail(type, clearOnFail))
        }

        this.errorText.value = ""

        executor.execute(task)
    }

    fun getSelectableSports(): List<String> = webScraper.selectableSports

    private fun lookupSchedule(currentSelection: String, onFail: () -> Unit): Task<out Any> {
        return object : Task<List<Match>>() {
            override fun call(): List<Match> {
                logger.debug("Loading schedule data")
                try {
                    val schedule = webScraper.getSchedule(currentSelection)
                    displayedSchedule.setAll(schedule)
                    return schedule
                } catch (cfx: ConnectionFailureException) {
                    onFail()
                    return emptyList()
                }
            }

            override fun run() {
                call()
            }
        }
    }

    private fun lookupStandings(currentSelection: String, onFail: () -> Unit): Task<out Any> {
        return object : Task<List<Standing>>() {
            override fun call(): List<Standing> {
                logger.debug("Loading standings data")
                try {
                    val standings = webScraper.getStandings(currentSelection)
                    displayedStandings.setAll(standings)
                    return standings
                } catch (cfx: ConnectionFailureException) {
                    onFail()
                    return emptyList()
                }
            }

            override fun run() {
                call()
            }
        }
    }

    private fun onFail(type: DisplayType, clearOnFail: Boolean): () -> Unit {
        val logError = {
            logger.debug("Failed to load {} data from server", type.toString())
            errorText.value = "Failed to load data from the server."
        }
        val changeDisplay = when {
            clearOnFail && type == SCHEDULE -> { -> displayedSchedule.clear() }
            clearOnFail && type == STANDINGS -> { -> displayedStandings.clear() }
            else -> { -> }
        }

        return {
            logError()
            changeDisplay()
        }
    }
}
