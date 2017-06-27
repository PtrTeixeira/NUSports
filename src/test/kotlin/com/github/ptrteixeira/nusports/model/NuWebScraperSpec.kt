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
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.tuple
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jsoup.nodes.Document

/**
 *
 */
class NuWebScraperSpec : Spek({
    val emptyScheduleCache = mutableMapOf<String, List<Match>>()
    val emptyStandingsCache = mutableMapOf<String, List<Standing>>()

    val buildMutableScheduleCache = { mutableMapOf<String, List<Match>>() }
    val buildMutableStandingsCache = { mutableMapOf<String, List<Standing>>() }

    val errorDocumentSource: DocumentSource = object : DocumentSource {
        override fun load(url: String): Document {
            throw ConnectionFailureException("Failed to connect")
        }
    }

    describe("clearCache") {
        val buildStandingsCache = {
            mutableMapOf(
                "key1" to listOf<Standing>(),
                "key2" to listOf<Standing>()
            )
        }
        val buildScheduleCache = {
            mutableMapOf(
                "key1" to listOf<Match>(),
                "key2" to listOf<Match>()
            )
        }

        given("an object in the cache") {
            it("should remove that object") {
                val standingsCache = buildStandingsCache()
                val scheduleCache = buildScheduleCache()
                val webScraper = NuWebScraper(standingsCache, scheduleCache, errorDocumentSource, CommonPool)

                webScraper.clearCache("key1")

                assertThat(standingsCache).containsOnlyKeys("key2")
                assertThat(scheduleCache).containsOnlyKeys("key2")
            }
        }
        given("an object not in the cache") {
            it("should do nothing") {
                val standingsCache = buildStandingsCache()
                val scheduleCache = buildScheduleCache()
                val webScraper = NuWebScraper(standingsCache, scheduleCache, errorDocumentSource, CommonPool)

                webScraper.clearCache("key3")

                assertThat(standingsCache).containsOnlyKeys("key1", "key2")
                assertThat(scheduleCache).containsOnlyKeys("key1", "key2")
            }
        }
    }

    describe("getSchedule") {
        val mockSource = LocalDocumentSource("test_schedule")

        given("a value in the cache") {
            val standingsCache = emptyStandingsCache
            val scheduleEntry = listOf(Match("date", "opponent", "result"))
            val scheduleCache = mutableMapOf("key1" to scheduleEntry)
            val webScraper = NuWebScraper(standingsCache, scheduleCache, errorDocumentSource, CommonPool)

            it("should use the value from the cache") {
                runBlocking {
                    assertThat(webScraper.getSchedule("key1"))
                        .isEqualTo(scheduleEntry)
                }
            }
        }
        given("a value not in the cache") {
            val standingsCache = buildMutableStandingsCache()
            val scheduleCache = buildMutableScheduleCache()
            val webScraper = NuWebScraper(standingsCache, scheduleCache, mockSource, CommonPool)
            var result: List<Match> = FXCollections.observableArrayList()

            beforeGroup { result = runBlocking { webScraper.getSchedule("Baseball") } }

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
                val webScraper = NuWebScraper(
                    emptyStandingsCache, emptyScheduleCache, errorDocumentSource, CommonPool)

                assertThatExceptionOfType(ConnectionFailureException::class.java)
                    .isThrownBy { runBlocking { webScraper.getSchedule("Baseball") } }
            }
        }

        context("when a game has a victor") {
            val source = LocalDocumentSource("test_schedule")
            val webScraper = NuWebScraper(
                buildMutableStandingsCache(), buildMutableScheduleCache(), source, CommonPool)
            it("outputs a core as a win or a loss") {
                runBlocking {
                    assertThat(webScraper.getSchedule("Baseball"))
                        .extracting("result")
                        .containsExactly("W 3 - 2")
                }
            }
        }
        context("when a game results in a tie") {
            val source = LocalDocumentSource("test_schedule_ties")
            val webScraper = NuWebScraper(
                buildMutableStandingsCache(), buildMutableScheduleCache(), source, CommonPool)
            it("outputs the score, but not as a win or a loss") {
                runBlocking {
                    assertThat(webScraper.getSchedule("Baseball"))
                        .extracting("result")
                        .containsExactly("2 - 2")
                }
            }
        }
        context("when a game has not yet been played") {
            val source = LocalDocumentSource("test_schedule_unplayed_games")
            val webScraper = NuWebScraper(
                buildMutableStandingsCache(), buildMutableScheduleCache(), source, CommonPool)
            it("outputs the empty string") {
                runBlocking {
                    assertThat(webScraper.getSchedule("Baseball"))
                        .extracting("result")
                        .containsExactly("")
                }
            }
        }
    }

    describe("getStandings") {
        val mockSource = LocalDocumentSource("test_standings")

        given("a value in the cache") {
            val standingsEntry = listOf(Standing("teamName", "conference", "overall"))
            val standingsCache = mutableMapOf("key1" to standingsEntry)
            val scheduleCache = emptyScheduleCache
            val webScraper = NuWebScraper(standingsCache, scheduleCache, errorDocumentSource, CommonPool)
            it("should use the value from the cache") {
                runBlocking {
                    assertThat(webScraper.getStandings("key1"))
                        .isEqualTo(standingsEntry)
                }
            }
        }
        given("a value not in the cache") {
            val standingsCache = buildMutableStandingsCache()
            val scheduleCache = buildMutableScheduleCache()
            val webScraper = NuWebScraper(standingsCache, scheduleCache, mockSource, CommonPool)

            val result = runBlocking { webScraper.getStandings("Baseball") }
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
                val webScraper = NuWebScraper(
                    emptyStandingsCache, emptyScheduleCache, errorDocumentSource, CommonPool)

                assertThatExceptionOfType(ConnectionFailureException::class.java)
                    .isThrownBy { runBlocking { webScraper.getStandings("Baseball") } }
            }
        }
    }

    describe("getSelectableSports") {
        it("should return a list of sports supported by this scraper") {
            val webScraper = NuWebScraper(emptyStandingsCache, emptyScheduleCache, errorDocumentSource, CommonPool)

            assertThat(webScraper.selectableSports)
                .containsExactlyInAnyOrder(
                    "Baseball", "Softball",
                    "Volleyball", "Field Hockey",
                    "Men's Basketball", "Women's Basketball",
                    "Men's Soccer", "Women's Soccer"
                )
        }
        it("should only contain sports supported by the rest of the scraper") {
            val webScraper = NuWebScraper(emptyStandingsCache, emptyScheduleCache, errorDocumentSource, CommonPool)

            webScraper.selectableSports.forEach { sport ->
                assertThat(webScraper.sportToClass(sport))
                    .isNotEmpty()
                assertThat(webScraper.sportToPath(sport))
                    .isNotEmpty()
            }
        }
    }
})
