/* Released under the MIT license, 2019 */

package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.model.WebScraper
import com.github.ptrteixeira.nusports.model.WebScraperFactory

class NuWebScraperFactory : WebScraperFactory {
    private val component: NuWebScraperComponent = DaggerNuWebScraperComponent.create()

    override fun build(): WebScraper {
        return component.providesNuWebScraper()
    }
}