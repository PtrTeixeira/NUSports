/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jsoup.nodes.Document
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.IOException

@Disabled("Tests broken by change in parsing code")
@DisplayName("Northeastern Web Scraper")
internal class NuWebScraperTest {
    @Nested
    @DisplayName("when the cache is cleared")
    inner class CacheClear {
        lateinit var scheduleCache: MutableMap<String, List<Match>>
        lateinit var standingsCache: MutableMap<String, List<Standing>>
        lateinit var webscraper: NuWebScraper

        @BeforeEach
        fun setUp() {
            scheduleCache = scheduleCache().apply {
                put("key1", listOf())
                put("key2", listOf())
            }
            standingsCache = standingsCache().apply {
                put("key1", listOf())
                put("key2", listOf())
            }
            webscraper = NuWebScraper(standingsCache, scheduleCache, errorSource())
        }

        @Test
        @DisplayName("it should remove an item from the cache")
        fun itemInCache() {
            assertThat(scheduleCache).containsOnlyKeys("key1", "key2")
            assertThat(standingsCache).containsOnlyKeys("key1", "key2")

            webscraper.clearCache("key1")

            assertThat(scheduleCache).containsOnlyKeys("key2")
            assertThat(standingsCache).containsOnlyKeys("key2")
        }
    }

    @Nested
    @DisplayName("when loading the schedule")
    inner class ScheduleLoad {
        @Test
        @DisplayName("it loads values from the cache first")
        fun loadsFromCache() {
            val scheduleEntry = listOf(Match("date", "opponent", "result"))
            val scheduleCache = scheduleCache().apply {
                put("key1", scheduleEntry)
            }
            val webScraper = NuWebScraper(standingsCache(), scheduleCache, errorSource())

            runBlocking {
                assertThat(webScraper.getSchedule("key1"))
                    .isEqualTo(scheduleEntry)
            }
        }

        @Test
        @DisplayName("it loads from the document source second")
        fun loadFromSource() {
            val webScraper = NuWebScraper(standingsCache(), scheduleCache(), localSource("test_schedule"))

            runBlocking {
                val result = webScraper.getSchedule("Baseball")
                assertThat(result)
                    .isNotNull()
                    .hasSize(1)
                    .extracting<Pair<String, String>> { Pair(it.opponent, it.result) }
                    .containsExactly(Pair("Oklahoma", "W 3 - 2"))
            }
        }

        @Test
        @DisplayName("it stores result from document source in the cache")
        fun storesInCache() {
            val scheduleCache = scheduleCache()
            val webScraper = NuWebScraper(standingsCache(), scheduleCache, localSource("test_schedule"))

            runBlocking {
                val result = webScraper.getSchedule("Baseball")
                assertThat(scheduleCache)
                    .hasSize(1)
                    .containsEntry("Baseball", result)
            }
        }

        @Test
        @DisplayName("it propagates ConnectionFailureExceptions")
        fun propagatesException() {
            val webScraper = NuWebScraper(standingsCache(), scheduleCache(), errorSource())

            assertThatExceptionOfType(ConnectionFailureException::class.java)
                .isThrownBy { runBlocking { webScraper.getSchedule("Baseball") } }
        }

        @Test
        @DisplayName("it shows the score for games with a winner")
        fun showsWinner() {
            val webScraper = NuWebScraper(standingsCache(), scheduleCache(), localSource("test_schedule"))

            runBlocking {
                assertThat(webScraper.getSchedule("Baseball"))
                    .extracting<String> { it.result }
                    .containsExactly("W 3 - 2")
            }
        }

        @Test
        @DisplayName("it shows a tie for tied games")
        fun showsTie() {
            val webScraper = NuWebScraper(standingsCache(), scheduleCache(), localSource("test_schedule_ties"))

            runBlocking {
                assertThat(webScraper.getSchedule("Baseball"))
                    .extracting<String> { it.result }
                    .containsExactly("2 - 2")
            }
        }

        @Test
        @DisplayName("it shows a blank string when the game hasn't been played")
        fun showsUnplayedGames() {
            val webScraper = NuWebScraper(standingsCache(), scheduleCache(), localSource("test_schedule_unplayed_games"))

            runBlocking {
                assertThat(webScraper.getSchedule("Baseball"))
                    .extracting<String> { it.result }
                    .containsExactly("")
            }
        }
    }

    @Nested
    @DisplayName("when loading the standings")
    inner class StandingsLoad {
        @Test
        @DisplayName("it loads values from the cache first")
        fun loadsFromCache() {
            val standingsEntry = listOf(Standing("teamName", "conference", "overall"))
            val standingsCache = standingsCache().apply {
                put("key1", standingsEntry)
            }
            val webScraper = NuWebScraper(standingsCache, scheduleCache(), errorSource())

            runBlocking {
                assertThat(webScraper.getStandings("key1"))
                    .isEqualTo(standingsEntry)
            }
        }

        @Test
        @DisplayName("it loads from the document source second")
        fun loadFromSource() {
            val webScraper = NuWebScraper(standingsCache(), scheduleCache(), localSource("test_standings"))

            runBlocking {
                val result = webScraper.getStandings("Baseball")
                assertThat(result)
                    .isNotNull()
                    .extracting<String> { it.teamName }
                    .containsExactly(
                        "UNCW", "William & Mary", "Elon", "James Madison",
                        "Northeastern", "Charleston", "Delaware", "Towson", "Hofstra"
                    )
            }
        }

        @Test
        @DisplayName("it stores result from document source in the cache")
        fun storesInCache() {
            val standingsCache = standingsCache()
            val webScraper = NuWebScraper(standingsCache, scheduleCache(), localSource("test_standings"))

            runBlocking {
                val result = webScraper.getStandings("Baseball")
                assertThat(standingsCache)
                    .hasSize(1)
                    .containsEntry("Baseball", result)
            }
        }

        @Test
        @DisplayName("it propagates ConnectionFailureExceptions")
        fun propagatesExns() {
            val webscraper = NuWebScraper(standingsCache(), scheduleCache(), errorSource())

            assertThatExceptionOfType(ConnectionFailureException::class.java)
                .isThrownBy { runBlocking { webscraper.getStandings("Baseball") } }
        }
    }

    @Nested
    @DisplayName("when getting selectable sports")
    inner class SelectableSports {
        val webScraper = NuWebScraper(standingsCache(), scheduleCache(), errorSource())
        @Test
        @DisplayName("it returns a list of supported sports")
        fun supportedSports() {
            assertThat(webScraper.selectableSports)
                .containsExactlyInAnyOrder(
                    "Baseball", "Softball",
                    "Volleyball", "Field Hockey",
                    "Men's Basketball", "Women's Basketball",
                    "Men's Soccer", "Women's Soccer"
                )
        }

        @Test
        @DisplayName("the returned sports are supported by the rest of the scraper")
        // Regression test to guard against misspelling things :c
        fun allAreSupported() {
            webScraper.selectableSports.forEach { sport ->
                assertThat(webScraper.sportToClass(sport))
                    .isNotEmpty()
                assertThat(webScraper.sportToPath(sport))
                    .isNotEmpty()
            }
        }
    }

    companion object {
        fun scheduleCache() = mutableMapOf<String, List<Match>>()
        fun standingsCache() = mutableMapOf<String, List<Standing>>()
        fun errorSource() = object : DocumentSource {
            override suspend fun load(url: String): Document {
                throw IOException("Failed to connect")
            }
        }
        fun localSource(path: String) = LocalDocumentSource(path)
    }
}
