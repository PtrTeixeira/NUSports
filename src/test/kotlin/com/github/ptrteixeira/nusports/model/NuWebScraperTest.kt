/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

import com.github.ptrteixeira.nusports.dao.IScheduleDao
import com.github.ptrteixeira.nusports.dao.IStandingsDao
import com.github.ptrteixeira.nusports.dao.TestScheduleDao
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.withTestContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito
import org.mockito.Mockito.mock

@DisplayName("Northeastern Web Scraper")
internal class NuWebScraperTest {
    private val sampleMatch = Match("date", "opponent", "result")
    private val sampleStanding = Standing("team", "conference", "overall")

    // Mocks
    private val scheduleDao = mock(IScheduleDao::class.java)
    private val standingsDao = mock(IStandingsDao::class.java)

    // SUT
    private val webScraper = NuWebScraper(scheduleDao, standingsDao)

    @BeforeEach
    fun reset() {
        Mockito.reset(scheduleDao, standingsDao)
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
    @ObsoleteCoroutinesApi
    @Disabled("It's a cool idea, but I don't actually want to add timeouts yet")
    fun `it cuts off loading after a time period`() = withTestContext {
        val scheduleDao = TestScheduleDao {
            delay(10000)
            throw IllegalStateException()
        }
        val webScraper = NuWebScraper(scheduleDao, standingsDao)

        try {
            runBlocking(this) {
                webScraper.getSchedule("sport")
                advanceTimeBy(6500)
            }
            fail<Unit>("Expected to raise cancellation exception")
        } catch (timeout: TimeoutCancellationException) {
            /* Test passes */
            assertThat(timeout)
                .hasMessageContaining("Timed out")
        }
    }
}
