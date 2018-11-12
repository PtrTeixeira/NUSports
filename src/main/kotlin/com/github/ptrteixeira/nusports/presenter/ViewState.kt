/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.presenter

import com.github.ptrteixeira.nusports.model.ConnectionFailureException
import com.github.ptrteixeira.nusports.model.Match
import com.github.ptrteixeira.nusports.model.Standing
import com.github.ptrteixeira.nusports.model.WebScraper
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.javafx.JavaFx
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import tornadofx.ViewModel
import tornadofx.onChange
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ViewState @Inject constructor(private val webScraper: WebScraper) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx

    val displayedSchedule: ObservableList<Match> = FXCollections.observableArrayList()
    val displayedStandings: ObservableList<Standing> = FXCollections.observableArrayList()
    val errorText: SimpleStringProperty = SimpleStringProperty("")
    val selectableSports: List<String> = webScraper.selectableSports

    val selectedSport = SimpleStringProperty(selectableSports[0])
    val isLoading = SimpleBooleanProperty(true)

    init {
        selectedSport.onChange { newSelection ->
            newSelection?.let { update(it) }
        }

        update(selectedSport.value)
    }

    private fun update(selectedSport: String) {
        logger.info("Updating table contents for sport {}", selectedSport)
        isLoading.set(true)

        async(Dispatchers.IO) {
            blockingUpdate(selectedSport)
        }.invokeOnCompletion {
            isLoading.set(false)
            logger.info("Done updating table contents")
        }
    }

    suspend fun blockingUpdate(selectedSport: String) {
        try {
            val schedule = webScraper.getSchedule(selectedSport)
            val standings = webScraper.getStandings(selectedSport)
            displayedSchedule.setAll(schedule)
            displayedStandings.setAll(standings)
        } catch (exn: ConnectionFailureException) {
            errorText.value = exn.message
            logger.error("Failed to load data from CAA", exn)
        }
    }

    fun reload() {
        webScraper.clearCache(selectedSport.value)
        update(selectedSport.value)
    }

    companion object {
        val logger: Logger = LogManager.getLogger()
    }
}
