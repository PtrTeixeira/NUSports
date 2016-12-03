package com.github.ptrteixeira.nusports.presenter

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
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import org.apache.logging.log4j.LogManager
import tornadofx.Controller
import java.util.concurrent.Executor

class MainController(private val executor: Executor,
                     private val webScraper: WebScraper) : Controller() {
    private val logger = LogManager.getLogger()

    val displayedSchedule : ObservableList<Match>
        = FXCollections.observableArrayList<Match>()
    val displayedStandings : ObservableList<Standing>
        = FXCollections.observableArrayList<Standing>()
    val errorText : SimpleStringProperty = SimpleStringProperty()

    fun lookup(type : DisplayType, currentSelection : String, clearOnFail: Boolean = false) {
        val task = when (type) {
            STANDINGS -> lookupSchedule(currentSelection)
            SCHEDULE -> lookupStandings(currentSelection)
        }

        this.errorText.value = ""
        task.onFailed = onError(type, clearOnFail)

        executor.execute(task)
    }

    fun getSelectableSports() : List<String> = webScraper.selectableSports

    private fun lookupSchedule(currentSelection: String) : Task<List<Match>> {
        return object : Task<List<Match>>() {
            override fun call(): List<Match> {
                val schedule = webScraper.getSchedule(currentSelection)
                displayedSchedule.setAll(schedule)

                return schedule
            }
        }
    }

    private fun lookupStandings(currentSelection: String) : Task<List<Standing>> {
        return object : Task<List<Standing>>() {
            override fun call(): List<Standing> {
                val standings = webScraper.getStandings(currentSelection)
                displayedStandings.setAll(standings)

                return standings;
            }
        }
    }

    private fun onError(type: DisplayType, clearOnFail: Boolean): EventHandler<WorkerStateEvent> {
        return EventHandler {
            val exn = it.source.exception
            logger.warn("Failed to connect", exn)
            errorText.set("Failed to load data.")

            if (clearOnFail) {
                when (type) {
                    STANDINGS -> displayedStandings.clear()
                    SCHEDULE -> displayedStandings.clear()
                }
            }
        }

    }
}
