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
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.runBlocking
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
            .willReturn(listOf<Match>())
        given(webScraper.getStandings(anyString()))
            .willReturn(listOf<Standing>())

        viewState = ViewState(MockWebScraper(webScraper), Unconfined)
    }

    @Test
    fun itClearsTheCacheOnReload() {
        runBlocking {
            viewState.reload()
        }

        verify(webScraper)
            .clearCache("sport 1")
    }

    @RepeatedTest(10)
    // Repeated because this test has a bad habit of being
    // flaky, in part because it sits on top of coroutines.
    fun itSetsTheErrorTextWhenAnExnOccurs() {
        given(webScraper.getSchedule("sport 2"))
            .willThrow(ConnectionFailureException("Failed to connect"))

        assertThat(viewState.errorText.value)
            .isEqualTo("")

        runBlocking {
            viewState.blockingUpdate("sport 2")
        }

        assertThat(viewState.errorText.value)
            .isEqualTo("Failed to connect")
    }

}
