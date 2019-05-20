/* Released under the MIT license, 2019 */

package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.model.WebScraper
import dagger.Component

@Component(modules = [NuWebScraperModule::class])
internal interface NuWebScraperComponent {
    fun providesNuWebScraper(): WebScraper
}