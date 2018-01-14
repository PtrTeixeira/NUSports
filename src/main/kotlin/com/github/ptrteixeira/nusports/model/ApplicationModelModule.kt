/* Released under the MIT license, $YEAR */

package com.github.ptrteixeira.nusports.model

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.CommonPool
import java.util.HashMap
import javax.inject.Named
import kotlin.coroutines.experimental.CoroutineContext

@Module
class ApplicationModelModule {
    @Provides
    internal fun provideWebScraper(nuWebScraper: NuWebScraper): WebScraper = nuWebScraper

    @Provides
    @Named(MODEL_COROUTINE_POOL)
    internal fun providesCoroutineContext(): CoroutineContext = CommonPool

    @Provides
    internal fun provideStandingsCache(): Map<String, List<Standing>> {
        return HashMap()
    }

    @Provides
    internal fun provideScheduleCache(): Map<String, List<Match>> {
        return HashMap()
    }

    @Provides
    internal fun provideDocumentSource(nuDocumentSource: NuDocumentSource): DocumentSource = nuDocumentSource

    companion object {
        const val MODEL_COROUTINE_POOL = "nusports.model.pool"
    }
}
