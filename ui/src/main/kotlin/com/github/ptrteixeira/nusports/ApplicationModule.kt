/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.model.WebScraper
import com.github.ptrteixeira.nusports.model.WebScraperFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.IllegalStateException
import java.util.ServiceLoader

@Module
internal object ApplicationModule {
    private val serviceLoader = ServiceLoader.load(WebScraperFactory::class.java)

    @Provides
    @JvmStatic
    fun providesWebScraper(): WebScraper {
        val possibleFactories = serviceLoader.iterator()
        if (possibleFactories.hasNext()) {
            return possibleFactories.next().build()
        } else {
            throw IllegalStateException("Couldn't find any registered web scrapers")
        }
    }

    @Provides
    @Reusable
    @JvmStatic
    fun providesIoScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO)
    }
}
