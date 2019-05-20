/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.model.WebScraper
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Module
internal object ApplicationModule {
    private val webScraperFactory = NuWebScraperFactory()

    @Provides
    @JvmStatic
    fun providesWebScraper(): WebScraper {
        return webScraperFactory.build()
    }

    @Provides
    @Reusable
    @JvmStatic
    fun providesIoScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO)
    }
}
