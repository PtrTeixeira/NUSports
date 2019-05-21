/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.presenter

import com.github.ptrteixeira.nusports.model.ConnectionError
import com.github.ptrteixeira.nusports.model.ConnectionFailureException
import com.github.ptrteixeira.nusports.model.InteractionEvent
import com.github.ptrteixeira.nusports.model.Match
import com.github.ptrteixeira.nusports.model.ReloadEvent
import com.github.ptrteixeira.nusports.model.Standing
import com.github.ptrteixeira.nusports.model.VisibleSport
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
internal class ViewStateTest {
    private val standing = Standing("Team", "Conference W-L", "Overall W-L")
    private val match = Match("Date", "Opponent", "Score")

    private val fakeIoDispatcher = TestCoroutineScope()
    private val webScraper: SyncWebScraper = mock(SyncWebScraper::class.java)
    private val viewState = ViewState(MockWebScraper(webScraper), fakeIoDispatcher)

    @Test
    fun itClearsTheCacheOnReload() {
        given(webScraper.getStandings("sport 1"))
                .willReturn(listOf(standing))
        given(webScraper.getSchedule("sport 1"))
                .willReturn(listOf(match))

        val viewUpdate = runBlocking {
            viewState.getViewUpdate(ReloadEvent("sport 1"))
        }

        val (schedule, standings) = (viewUpdate as VisibleSport)

        assertThat(standings)
                .containsExactly(standing)
        assertThat(schedule)
                .containsExactly(match)
        then(webScraper)
                .should()
                .clearCache("sport 1")
    }

    @Test
    fun itReturnsAnErrorEventWhenAnExceptionOccurs() {
        given(webScraper.getSchedule("sport 2"))
                .willThrow(ConnectionFailureException("Failed to connect"))

        runBlockingTest {
            val result = viewState.getViewUpdate(InteractionEvent("sport 2"))

            assertThat(result as ConnectionError)
                    .extracting(ConnectionError::errorText)
                    .isEqualTo("Failed to connect")
        }
    }
}
