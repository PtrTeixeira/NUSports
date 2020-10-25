/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.model.FullWebScraper
import com.github.ptrteixeira.nusports.model.WebScraperFactory
import com.github.ptrteixeira.nusports.presenter.ViewState
import com.github.ptrteixeira.nusports.view.Body
import com.github.ptrteixeira.nusports.view.SportsWorkspace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import tornadofx.App
import tornadofx.DIContainer
import tornadofx.FX
import tornadofx.UIComponent
import java.util.ServiceLoader
import kotlin.reflect.KClass

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class SportsApp : App(SportsWorkspace::class) {
    override fun onBeforeShow(view: UIComponent) {
        workspace.dock<Body>()
    }

    override fun stop() {
        System.exit(0)
    }

    companion object {
        private val logger: Logger = LogManager.getLogger()

        @JvmStatic
        fun main(args: Array<String>) {
            logger.info("Starting application")

            val serviceLoader = ServiceLoader.load(WebScraperFactory::class.java)
            val webScrapers = serviceLoader
                .asIterable()
                .map(WebScraperFactory::build)

            val viewState = ViewState(
                FullWebScraper(webScrapers),
                CoroutineScope(Dispatchers.IO)
            )
            FX.dicontainer = object : DIContainer {
                @Suppress("UNCHECKED_CAST")
                override fun <T : Any> getInstance(type: KClass<T>): T = when (type) {
                    ViewState::class -> viewState as T
                    else -> throw IllegalArgumentException()
                }
            }

            launch(SportsApp::class.java, *args)
        }
    }
}
