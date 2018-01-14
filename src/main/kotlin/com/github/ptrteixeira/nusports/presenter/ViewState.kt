/* Released under the MIT license, $YEAR */

package com.github.ptrteixeira.nusports.presenter

import com.github.ptrteixeira.nusports.ApplicationModule
import com.github.ptrteixeira.nusports.model.ConnectionFailureException
import com.github.ptrteixeira.nusports.model.Match
import com.github.ptrteixeira.nusports.model.Standing
import com.github.ptrteixeira.nusports.model.WebScraper
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.experimental.launch
import tornadofx.ViewModel
import tornadofx.onChange
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.experimental.CoroutineContext

class ViewState @Inject
constructor(
    private val webScraper: WebScraper,
    @Named(ApplicationModule.UI_COROUTINE_POOL) private val context: CoroutineContext
) : ViewModel() {
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
        isLoading.set(true)
        launch(context) {
            blockingUpdate(selectedSport)
        }.invokeOnCompletion {
            isLoading.set(false)
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
        }
    }

    fun reload() {
        webScraper.clearCache(selectedSport.value)
        update(selectedSport.value)
    }
}
