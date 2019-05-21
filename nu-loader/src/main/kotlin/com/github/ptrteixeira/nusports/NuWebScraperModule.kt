/* Released under the MIT license, 2019 */

package com.github.ptrteixeira.nusports

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.ptrteixeira.nusports.dao.CaaService
import com.github.ptrteixeira.nusports.dao.DocumentSource
import com.github.ptrteixeira.nusports.dao.IScheduleDao
import com.github.ptrteixeira.nusports.dao.IStandingsDao
import com.github.ptrteixeira.nusports.dao.NuDocumentSource
import com.github.ptrteixeira.nusports.dao.ScheduleDao
import com.github.ptrteixeira.nusports.dao.StandingsDao
import com.github.ptrteixeira.nusports.model.Match
import com.github.ptrteixeira.nusports.model.NuWebScraper
import com.github.ptrteixeira.nusports.model.Standing
import com.github.ptrteixeira.nusports.model.WebScraper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.time.Clock
import java.util.HashMap

@Module
internal abstract class NuWebScraperModule {
    @Binds
    abstract fun providesWebScraper(nuWebScraper: NuWebScraper): WebScraper

    @Binds
    abstract fun providesDocumentSource(nuDocumentSource: NuDocumentSource): DocumentSource

    @Binds
    abstract fun providesScheduleDao(scheduleDao: ScheduleDao): IScheduleDao

    @Binds
    abstract fun providesStandingsDao(standingsDao: StandingsDao): IStandingsDao

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideStandingsCache(): Map<String, List<Standing>> {
            return HashMap()
        }

        @Provides
        @JvmStatic
        fun provideScheduleCache(): Map<String, List<Match>> {
            return HashMap()
        }

        @Provides
        @JvmStatic
        fun providesClock(): Clock {
            return Clock.systemDefaultZone()
        }

        @Provides
        @Reusable
        @JvmStatic
        fun providesObjectMapper(): ObjectMapper {
            val objectMapper = ObjectMapper()

            objectMapper.registerModule(KotlinModule())
            objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
            return objectMapper
        }

        @Provides
        @Reusable
        @JvmStatic
        fun providesHttpClient(): OkHttpClient {
            return OkHttpClient()
        }

        @Provides
        @Reusable
        @JvmStatic
        fun providesRetrofit(objectMapper: ObjectMapper, client: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl("https://caasports.com")
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                    .build()
        }

        @Provides
        @Reusable
        @JvmStatic
        fun providesCaaService(retrofit: Retrofit): CaaService {
            return retrofit.create(CaaService::class.java)
        }
    }
}