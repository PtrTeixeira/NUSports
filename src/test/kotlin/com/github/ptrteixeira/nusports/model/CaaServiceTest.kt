/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.time.Clock
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

internal class CaaServiceTest {
    private val objectMapper = ObjectMapper().apply {
        registerModule(KotlinModule())

        enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
    }
    private val retrofit = Retrofit.Builder()
            .baseUrl("https://caasports.com")
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build()

    @Test
    fun `it works`() {
        val caaService = retrofit.create(CaaService::class.java)
        val clock = Clock.systemDefaultZone()
        val start_t = ZonedDateTime.now(clock).minus(3, ChronoUnit.MONTHS)
        val end_t = ZonedDateTime.now(clock).plus(1, ChronoUnit.MONTHS)

        print(start_t.format(DateTimeFormatter.ISO_OFFSET_DATE))
        val start = start_t.format(DateTimeFormatter.ISO_OFFSET_DATE)//"2018-09-30T11:25:17-5:00"
        val end = end_t.format(DateTimeFormatter.ISO_OFFSET_DATE)//"2018-12-30T23:59:59-05:00"
        val sportId = "0"
        val schoolId = "0"

        val response = caaService.getSchedule(start, end, sportId, schoolId).execute().body()
        println(response)
    }
}