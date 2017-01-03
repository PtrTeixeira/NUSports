package com.github.ptrteixeira.nusports.presenter

import com.github.ptrteixeira.nusports.model.MockWebScraper
import com.github.ptrteixeira.nusports.view.DisplayType
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.concurrent.Executor

internal class MainControllerSpec : SubjectSpek<MainController>({
    val scraper = MockWebScraper()
    val immediateExecutor = Executor { it.run() }

    subject { MainController(immediateExecutor, scraper) }

    describe(".lookup") {
        beforeEach { scraper.reset() }
        context("when Standings is displayed") {
            beforeEach { subject.lookup(DisplayType.STANDINGS, "Sport 1", false) }
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
                beforeEach { scraper.failNextRequest() }
                context("when set to clear on fail") {
                    beforeEach { subject.lookup(DisplayType.STANDINGS, "Sport 1", clearOnFail = true) }
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
                    beforeEach { subject.lookup(DisplayType.STANDINGS, "Sport 1", clearOnFail = false) }
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
            beforeEach { subject.lookup(DisplayType.SCHEDULE, "Sport 1", false) }
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
                beforeEach { scraper.failNextRequest() }
                context("when set to clear on fail") {
                    beforeEach { subject.lookup(DisplayType.SCHEDULE, "Sport 1", clearOnFail = true) }
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
                    beforeEach { subject.lookup(DisplayType.SCHEDULE, "Sport 1", clearOnFail = false) }
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
