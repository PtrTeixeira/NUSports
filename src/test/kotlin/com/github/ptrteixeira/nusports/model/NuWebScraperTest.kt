/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

import com.github.ptrteixeira.nusports.dao.IScheduleDao
import com.github.ptrteixeira.nusports.dao.IStandingsDao
import com.github.ptrteixeira.nusports.dao.FakeScheduleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito
import org.mockito.Mockito.mock

@DisplayName("Northeastern Web Scraper")
@ObsoleteCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class NuWebScraperTest {
    private val sampleMatch = Match("date", "opponent", "result")
    private val sampleStanding = Standing("team", "conference", "overall")
    private val fakeUiThread = newSingleThreadContext("UI Thread")

    // Mocks
    private val scheduleDao = mock(IScheduleDao::class.java)
    private val standingsDao = mock(IStandingsDao::class.java)

    // SUT
    private val webScraper = NuWebScraper(scheduleDao, standingsDao)

    @BeforeAll
    @ExperimentalCoroutinesApi
    fun setUp() {
        Dispatchers.setMain(fakeUiThread)
    }

    @BeforeEach
    fun reset() {
        Mockito.reset(scheduleDao, standingsDao)
    }

    @AfterAll
    @ExperimentalCoroutinesApi
    fun tearDown() {
        Dispatchers.resetMain()
        fakeUiThread.close()
    }

    @Test
    fun `it removes items from each dao on clear`() {
        // when
        webScraper.clearCache("key1")

        then(scheduleDao)
                .should()
                .clear("key1")
        then(standingsDao)
                .should()
                .clear("key1")
    }

    @Test
    fun `it loads the schedule from the schedule dao`() {
        runBlocking {
            given(scheduleDao.get("key1"))
                    .willReturn(listOf(sampleMatch))

            val result = webScraper.getSchedule("key1")

            assertThat(result)
                    .containsExactly(sampleMatch)
        }
    }

    @Test
    fun `it loads the standings from the standings dao`() {
        runBlocking {
            given(standingsDao.get("key1"))
                    .willReturn(listOf(sampleStanding))

            val result = webScraper.getStandings("key1")

            assertThat(result)
                    .containsExactly(sampleStanding)
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    @Disabled("It's a cool idea, but I don't actually want to add timeouts yet")
    fun `it cuts off loading after a time period`() = runBlockingTest {
        val scheduleDao = FakeScheduleDao {
            delay(1000)
            throw IllegalStateException()
        }
        val webScraper = NuWebScraper(scheduleDao, standingsDao)

        try {
            launch {
                webScraper.getSchedule("sport")
                advanceTimeBy(200)
            }
            fail<Unit>("Expected to raise cancellation exception")
        } catch (timeout: TimeoutCancellationException) {
            assertThat(timeout)
                    .hasMessageContaining("Timed out")
        }
    }
}
