/* Released under the MIT license, 2019 */

package com.github.ptrteixeira.nusports

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.ptrteixeira.nusports.dao.CaaService
import com.github.ptrteixeira.nusports.dao.NuDocumentSource
import com.github.ptrteixeira.nusports.dao.ScheduleDao
import com.github.ptrteixeira.nusports.dao.StandingsDao
import com.github.ptrteixeira.nusports.model.Match
import com.github.ptrteixeira.nusports.model.NuWebScraper
import com.github.ptrteixeira.nusports.model.Standing
import com.github.ptrteixeira.nusports.model.WebScraper
import com.github.ptrteixeira.nusports.model.WebScraperFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.time.Clock

class NuWebScraperFactory : WebScraperFactory {
    override fun build(): WebScraper {
        val scheduleCache = mutableMapOf<String, List<Match>>()
        val standingsCache = mutableMapOf<String, List<Standing>>()
        val clock = Clock.systemDefaultZone()

        val client = OkHttpClient()
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(KotlinModule())
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("https://caasports.com")
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build()
        val caaSource = retrofit.create(CaaService::class.java)
        val scheduleDao = ScheduleDao(
            scheduleCache,
            clock,
            caaSource
        )

        val documentSource = NuDocumentSource(client)
        val standingsDao = StandingsDao(standingsCache, documentSource)

        return NuWebScraper(scheduleDao, standingsDao)
    }
}