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

import com.github.ptrteixeira.nusports.model.MockWebScraper
import com.github.ptrteixeira.nusports.view.DisplayType
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import java.util.concurrent.Executor

internal class MainControllerSpec : SubjectSpek<MainController>({
    val scraper = MockWebScraper()
    val immediateExecutor = Executor { it.run() }

    subject { MainController(immediateExecutor, scraper) }

    describe(".lookup") {
        beforeEachTest { scraper.reset() }
        context("when Standings is displayed") {
            beforeEachTest { subject.lookup(DisplayType.STANDINGS, "Sport 1", false) }
            it("should make a query about the standings") {
                assertThat(scraper.standingsRequests)
                    .hasSize(1)
                    .allSatisfy { it == "Sport 1" }
                assertThat(scraper.scheduleRequests)
                    .isEmpty()
            }
            context("when the query succeeds") {
                it("should clear the error text") {
                    subject.lookup(DisplayType.STANDINGS, "Sport 1", false)

                    assertThat(subject.errorText.get())
                        .isEmpty()
                }
                it("should update the standings table") {
                    assertThat(subject.displayedStandings)
                        .isNotEmpty
                }
            }
            context("when the query fails") {
                beforeEachTest { scraper.failNextRequest() }
                context("when set to clear on fail") {
                    beforeEachTest { subject.lookup(DisplayType.STANDINGS, "Sport 1", clearOnFail = true) }
                    it("should show an error message") {
                        assertThat(subject.errorText.get())
                            .isNotEmpty()
                    }
                    it("should clear the standings table") {
                        assertThat(subject.displayedStandings)
                            .isEmpty()
                    }
                }
                context("when not set to clear on fail") {
                    beforeEachTest { subject.lookup(DisplayType.STANDINGS, "Sport 1", clearOnFail = false) }
                    it("should show an error message") {
                        assertThat(subject.errorText.get())
                            .isNotEmpty()
                    }
                    it("should not modify the standings table") {
                        assertThat(subject.displayedStandings)
                            .isNotEmpty()
                    }
                }
            }
        }
        context("when Schedule is displayed") {
            beforeEachTest { subject.lookup(DisplayType.SCHEDULE, "Sport 1", false) }
            it("should make a query about the schedule") {
                assertThat(scraper.scheduleRequests)
                    .hasSize(1)
                    .allSatisfy { it == "Sport 1" }
                assertThat(scraper.standingsRequests)
                    .isEmpty()
            }
            context("when the query succeeds") {
                it("should clear the error text") {
                    assertThat(subject.errorText.get())
                        .isEmpty()
                }
                it("should update the schedule table") {
                    assertThat(subject.displayedSchedule)
                        .isNotEmpty
                }
            }
            context("when the query fails") {
                beforeEachTest { scraper.failNextRequest() }
                context("when set to clear on fail") {
                    beforeEachTest { subject.lookup(DisplayType.SCHEDULE, "Sport 1", clearOnFail = true) }
                    it("should show an error message") {
                        assertThat(subject.errorText.get())
                            .isNotEmpty()
                    }
                    it("should clear the schedule table") {
                        assertThat(subject.displayedSchedule)
                            .isEmpty()
                    }
                }
                context("when not set to clear on fail") {
                    beforeEachTest { subject.lookup(DisplayType.SCHEDULE, "Sport 1", clearOnFail = false) }
                    it("should show an error message") {
                        assertThat(subject.errorText.get())
                            .isNotEmpty()
                    }
                    it("should not modify the schedule table") {
                        assertThat(subject.displayedSchedule)
                            .isNotEmpty()
                    }
                }
            }
        }
    }

    describe(".getSelectableSports") {
        it("Should return the sports supported by the given web scraper") {
            assertThat(subject.getSelectableSports())
                .isEqualTo(scraper.selectableSports)
        }
    }
})
