/* Released under the MIT license, 2019 */

package com.github.ptrteixeira.nusports.model

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

internal class FullWebScraperTest {
    @Test
    fun `it throws an exception if no web scrapers are discovered`() {
        assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy {
                    FullWebScraper(listOf())
                }
    }

    @Test
    fun `it throws an exception of multiple web scrapers claim the same sport`() {
        val webScraperOne = mock(WebScraper::class.java)
        given(webScraperOne.selectableSports)
                .willReturn(listOf("soccer"))

        val webScraperTwo = mock(WebScraper::class.java)
        given(webScraperTwo.selectableSports)
                .willReturn(listOf("soccer"))

        assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy {
                    FullWebScraper(listOf(webScraperOne, webScraperTwo))
                }
    }

    @Test
    fun `it allows selecting any sport supported by its components`() {
        val webScraper = FullWebScraper(listOf(
                FakeWebScraper(listOf("soccer")),
                FakeWebScraper(listOf("hurling", "rugby", "cricket"))
        ))

        assertThat(webScraper.selectableSports)
                .containsExactlyInAnyOrder(
                        "soccer",
                        "hurling",
                        "rugby",
                        "cricket"
                )
    }

    @Test
    fun `it throws an exception when selecting the schedule for an unsupported sport`() {
        val webScraper = FullWebScraper(listOf(
                FakeWebScraper(listOf("soccer")),
                FakeWebScraper(listOf("hurling", "rugby", "cricket"))
        ))

        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy {
                    runBlocking {
                        webScraper.getSchedule("basketball")
                    }
                }
    }

    @Test
    fun `it throws an exception when selecting standings for an unsupported sport`() {
        val webScraper = FullWebScraper(listOf(
                FakeWebScraper(listOf("soccer")),
                FakeWebScraper(listOf("hurling", "rugby", "cricket"))
        ))

        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy {
                    runBlocking {
                        webScraper.getStandings("basketball")
                    }
                }
    }

    @Test
    fun `it throws an exception when clearing the cache for an unsupported sport`() {
        val webScraper = FullWebScraper(listOf(
                FakeWebScraper(listOf("soccer")),
                FakeWebScraper(listOf("hurling", "rugby", "cricket"))
        ))

        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { webScraper.clearCache("basketball") }
    }

    @Test
    fun `it allows selecting the schedule for a supported sport`() {
        val webScraper = FullWebScraper(listOf(
                FakeWebScraper(listOf("soccer")),
                FakeWebScraper(listOf("hurling", "rugby", "cricket"))
        ))

        val result = runBlocking {
            webScraper.getSchedule("soccer")
        }

        assertThat(result)
                .isEmpty()
    }

    @Test
    fun `it allows selecting the standings for a supported sport`() {
        val webScraper = FullWebScraper(listOf(
                FakeWebScraper(listOf("soccer")),
                FakeWebScraper(listOf("hurling", "rugby", "cricket"))
        ))

        val result = runBlocking {
            webScraper.getStandings("soccer")
        }

        assertThat(result)
                .isEmpty()
    }

    @Test
    fun `it allows clearing the cache for a supported sport`() {
        val webScraperOne = FakeWebScraper(listOf("soccer"))
        val webScraperTwo = FakeWebScraper(listOf("hurling", "rugby", "cricket"))

        val webScraper = FullWebScraper(listOf(
                webScraperOne, webScraperTwo
        ))

        webScraper.clearCache("hurling")

        assertThat(webScraperTwo.clearCacheCalled)
                .isTrue()
    }
}

internal class FakeWebScraper(override val selectableSports: List<String>) : WebScraper {
    var clearCacheCalled = false

    override suspend fun getStandings(sport: String): List<Standing> {
        return listOf()
    }

    override suspend fun getSchedule(sport: String): List<Match> {
        return listOf()
    }

    override fun clearCache(sport: String) {
        clearCacheCalled = true
    }
}