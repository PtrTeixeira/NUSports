/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.presenter

import com.github.ptrteixeira.nusports.model.ConnectionError
import com.github.ptrteixeira.nusports.model.ConnectionFailureException
import com.github.ptrteixeira.nusports.model.InteractionEvent
import com.github.ptrteixeira.nusports.model.ReloadEvent
import com.github.ptrteixeira.nusports.model.SportChangeEvent
import com.github.ptrteixeira.nusports.model.UpdateEvent
import com.github.ptrteixeira.nusports.model.VisibleSport
import com.github.ptrteixeira.nusports.model.WebScraper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ViewState(
    private val webScraper: WebScraper,
    private val ioScope: CoroutineScope
) : CoroutineScope by ioScope {
    val selectableSports: List<String> = webScraper.selectableSports

    suspend fun getViewUpdate(sportChangeEvent: SportChangeEvent): UpdateEvent {
        return when (sportChangeEvent) {
            is InteractionEvent -> loadNewSport(sportChangeEvent.newSport)
            is ReloadEvent -> {
                val (newSport) = sportChangeEvent
                reload(newSport)
                loadNewSport(newSport)
            }
        }
    }

    private suspend fun loadNewSport(newSport: String): UpdateEvent {
        logger.info("Loading sport {}", newSport)

        return try {
            coroutineScope {
                val schedule = async { webScraper.getSchedule(newSport) }
                val results = async { webScraper.getStandings(newSport) }

                VisibleSport(schedule.await(), results.await())
            }
        } catch (exn: ConnectionFailureException) {
            ConnectionError(exn.message ?: "Failed to connect", exn)
        }
    }

    private fun reload(newSport: String) {
        webScraper.clearCache(newSport)
    }

    companion object {
        val logger: Logger = LogManager.getLogger()
    }
}
