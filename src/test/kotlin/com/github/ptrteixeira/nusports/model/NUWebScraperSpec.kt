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
package com.github.ptrteixeira.nusports.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.tuple
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException

/**
 *
 */
class NUWebScraperSpec : Spek({
    val emptyScheduleCache = emptyMap<String, ObservableList<Match>>()
    val emptyStandingsCache = emptyMap<String, ObservableList<Standing>>()

    val buildMutableScheduleCache = { mutableMapOf<String, ObservableList<Match>>() }
    val buildMutableStandingsCache = { mutableMapOf<String, ObservableList<Standing>>() }

    val errorDocumentSource = DocumentSource { throw IOException() }

    describe("clearCache") {
        val buildStandingsCache = {
            mutableMapOf(
                "key1" to FXCollections.emptyObservableList<Standing>(),
                "key2" to FXCollections.emptyObservableList<Standing>()
            )
        }
        val buildScheduleCache = {
            mutableMapOf(
                "key1" to FXCollections.emptyObservableList<Match>(),
                "key2" to FXCollections.emptyObservableList<Match>()
            )
        }

        given("an object in the cache") {
            it("should remove that object") {
                val standingsCache = buildStandingsCache()
                val scheduleCache = buildScheduleCache()
                val webScraper = NUWebScraper(standingsCache, scheduleCache, null)

                webScraper.clearCache("key1")

                assertThat(standingsCache).containsOnlyKeys("key2")
                assertThat(scheduleCache).containsOnlyKeys("key2")
            }
        }
        given("an object not in the cache") {
            it("should do nothing") {
                val standingsCache = buildStandingsCache()
                val scheduleCache = buildScheduleCache()
                val webScraper = NUWebScraper(standingsCache, scheduleCache, null)

                webScraper.clearCache("key3")

                assertThat(standingsCache).containsOnlyKeys("key1", "key2")
                assertThat(scheduleCache).containsOnlyKeys("key1", "key2")
            }
        }
        given("a null value") {
            it("should throw a NullPointerException") {
                val standingsCache = buildStandingsCache()
                val scheduleCache = buildScheduleCache()
                val webScraper = NUWebScraper(standingsCache, scheduleCache, null)

                assertThatExceptionOfType(NullPointerException::class.java)
                    .isThrownBy { webScraper.clearCache(null) }
            }
        }
    }

    describe("getSchedule") {
        val mockSource = DocumentSource { url ->
            Jsoup.parse(File("src/test/resources/test_schedule.html"), "UTF8", ".")
        }

        given("a value in the cache") {
            val standingsCache = emptyStandingsCache
            val scheduleEntry = FXCollections
                .singletonObservableList(Match("date", "opponent", "result"))
            val scheduleCache = mutableMapOf("key1" to scheduleEntry)
            val webScraper = NUWebScraper(standingsCache, scheduleCache, errorDocumentSource)
            it("should use the value from the cache") {
                assertThat(webScraper.getSchedule("key1"))
                    .isEqualTo(scheduleEntry)
            }
        }
        given("a value not in the cache") {
            val standingsCache = buildMutableStandingsCache()
            val scheduleCache = buildMutableScheduleCache()
            val webScraper = NUWebScraper(standingsCache, scheduleCache, mockSource)

            val result = webScraper.getSchedule("Baseball")
            it("should fetch it from an external source") {
                assertThat(result)
                    .isNotNull()
                    .hasSize(1)
                    .extracting("opponent", "result")
                    .containsExactly(tuple("Oklahoma", "W 3 - 2"))
            }
            it("should store the value in the cache") {
                assertThat(scheduleCache)
                    .hasSize(1)
                    .containsEntry("Baseball", result)
                assertThat(standingsCache)
                    .isEmpty()
            }
        }
        context("when an IO error occurs") {
            it("should throw an ConnectionFailureException") {
                val webScraper = NUWebScraper(
                    emptyStandingsCache, emptyScheduleCache, errorDocumentSource)

                assertThatExceptionOfType(ConnectionFailureException::class.java)
                    .isThrownBy { webScraper.getSchedule("Baseball") }
            }
        }

        context("when a game has a victor") {
            val source = DocumentSource {
                Jsoup.parse(File("src/test/resources/test_schedule.html"), "UTF8", ".")
            }
            val webScraper = NUWebScraper(
                buildMutableStandingsCache(), buildMutableScheduleCache(), source)
            it("outputs a core as a win or a loss") {
                assertThat(webScraper.getSchedule("Baseball"))
                    .extracting("result")
                    .containsExactly("W 3 - 2")
            }
        }
        context("when a game results in a tie") {
            val source = DocumentSource {
                Jsoup.parse(File("src/test/resources/test_schedule_ties.html"), "UTF8", ".")
            }
            val webScraper = NUWebScraper(
                buildMutableStandingsCache(), buildMutableScheduleCache(), source)
            it("outputs the score, but not as a win or a loss") {
                assertThat(webScraper.getSchedule("Baseball"))
                    .extracting("result")
                    .containsExactly("2 - 2")
            }
        }
        context("when a game has not yet been played") {
            val source = DocumentSource {
                Jsoup.parse(File("src/test/resources/test_schedule_unplayed_games.html"), "UTF8", ".")
            }
            val webScraper = NUWebScraper(
                buildMutableStandingsCache(), buildMutableScheduleCache(), source)
            it("outputs the empty string") {
                assertThat(webScraper.getSchedule("Baseball"))
                    .extracting("result")
                    .containsExactly("")
            }
        }
    }

    describe("getStandings") {
        val mockSource = DocumentSource {
            Jsoup.parse(File("src/test/resources/test_standings.html"), "UTF8", ".")
        }

        given("a value in the cache") {
            val standingsEntry = FXCollections
                .singletonObservableList(Standing("teamName", "conference", "overall"))
            val standingsCache = mutableMapOf("key1" to standingsEntry)
            val scheduleCache = emptyScheduleCache
            val webScraper = NUWebScraper(standingsCache, scheduleCache, errorDocumentSource)
            it("should use the value from the cache") {
                assertThat(webScraper.getStandings("key1"))
                    .isEqualTo(standingsEntry)
            }
        }
        given("a value not in the cache") {
            val standingsCache = buildMutableStandingsCache()
            val scheduleCache = buildMutableScheduleCache()
            val webScraper = NUWebScraper(standingsCache, scheduleCache, mockSource)

            val result = webScraper.getStandings("Baseball")
            it("should fetch it from an external source") {
                assertThat(result)
                    .isNotNull()
                    .extracting("teamName")
                    .containsExactly(
                        "UNCW", "William & Mary", "Elon", "James Madison",
                        "Northeastern", "Charleston", "Delaware", "Towson", "Hofstra"
                    )
            }
            it("should store the value in the cache") {
                assertThat(standingsCache)
                    .hasSize(1)
                    .containsEntry("Baseball", result)
                assertThat(scheduleCache).isEmpty()
            }
        }
        context("when an IO error occurs") {
            it("should throw a ConnectionFailureException") {
                val webScraper = NUWebScraper(
                    emptyStandingsCache, emptyScheduleCache, errorDocumentSource)

                assertThatExceptionOfType(ConnectionFailureException::class.java)
                    .isThrownBy { webScraper.getStandings("Baseball") }
            }
        }
    }

    describe("getSelectableSports") {
        it("should return a list of sports supported by this scraper") {
            val webScraper = NUWebScraper(null, null, null)

            assertThat(webScraper.selectableSports)
                .containsExactlyInAnyOrder(
                    "Baseball", "Softball",
                    "Volleyball", "Field Hockey",
                    "Men's Basketball", "Women's Basketball",
                    "Men's Soccer", "Women's Soccer"
                )
        }
        it("should only contain sports supported by the rest of the scraper") {
            val webScraper = NUWebScraper(null, null, null)

            webScraper.selectableSports.forEach { sport ->
                assertThat(webScraper.sportToClass(sport))
                    .isNotEmpty()
                assertThat(webScraper.sportToPath(sport))
                    .isNotEmpty()
            }
        }
    }
})
