/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.presenter

import com.github.ptrteixeira.nusports.model.ConnectionFailureException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

internal class ViewStateTest {
    @Mock
    lateinit var webScraper: SyncWebScraper
    lateinit var viewState: ViewState

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        given(webScraper.selectableSports())
                .willReturn(listOf("sport 1", "sport 2"))
        given(webScraper.getSchedule(anyString()))
                .willReturn(listOf())
        given(webScraper.getStandings(anyString()))
                .willReturn(listOf())

        viewState = ViewState(MockWebScraper(webScraper), coroutineContext = Dispatchers.Default)
    }

    @Test
    fun itClearsTheCacheOnReload() {
        runBlocking {
            viewState.reload()

            verify(webScraper)
                    .clearCache("sport 1")
        }
    }

    // Repeated because this test has historically been kinda flaky
    @RepeatedTest(20)
    fun itSetsTheErrorTextWhenAnExnOccurs() {
        runBlocking {
            delay(200)
            given(webScraper.getSchedule("sport 2"))
                    .willThrow(ConnectionFailureException("Failed to connect"))

            assertThat(viewState.errorText.value)
                    .isBlank()

            viewState.blockingUpdate("sport 2")

            assertThat(viewState.errorText.value)
                    .isEqualTo("Failed to connect")
        }
    }
}
