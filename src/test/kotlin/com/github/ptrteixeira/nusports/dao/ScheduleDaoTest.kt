/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.dao

import com.github.ptrteixeira.nusports.model.ConnectionFailureException
import com.github.ptrteixeira.nusports.model.Match
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import retrofit2.mock.Calls
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class ScheduleDaoTest {
    private val caaService = mock(CaaService::class.java)
    private val cache = mutableMapOf<String, List<Match>>()
    private val clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)
    private val scheduleDao = ScheduleDao(cache, clock, caaService)

    @BeforeEach
    fun reset() {
        Mockito.reset(caaService)
        cache.clear()
    }

    @Test
    fun `it does not save to the cache after errors`() {
        given(caaService.getSchedule(anyString(), anyString(), anyString(), anyString()))
            .willReturn(Calls.failure(IOException()))

        // when
        assertThatExceptionOfType(ConnectionFailureException::class.java)
            .isThrownBy { runBlocking { scheduleDao.get("sport-id") } }

        // then
        assertThat(cache)
            .isEmpty()
    }

    @Test
    fun `it loads from the cache first if possible`() = runBlocking<Unit> {
        cache["sport-id"] = emptyList()

        // when
        scheduleDao.get("sport-id")

        then(caaService)
            .should(never())
            .getSchedule(anyString(), anyString(), anyString(), anyString())
    }

    @Test
    fun `it saves results into the cache`() = runBlocking<Unit> {
        given(caaService.getSchedule(anyString(), anyString(), anyString(), anyString()))
            .willReturn(Calls.response(emptyList()))

        // when
        scheduleDao.get("sport-id")

        // then
        assertThat(cache)
            .containsOnlyKeys("sport-id")
    }
}