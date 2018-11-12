/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.presenter.ViewState
import com.github.ptrteixeira.nusports.view.Body
import com.github.ptrteixeira.nusports.view.SportsWorkspace
import javafx.application.Application
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import tornadofx.App
import tornadofx.DIContainer
import tornadofx.FX
import tornadofx.UIComponent
import kotlin.reflect.KClass

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

            val component: ApplicationComponent = DaggerApplicationComponent.create()

            FX.dicontainer = object : DIContainer {
                @Suppress("UNCHECKED_CAST")
                override fun <T : Any> getInstance(type: KClass<T>): T = when (type) {
                    ViewState::class -> component.viewState() as T
                    else -> throw IllegalArgumentException()
                }
            }

            Application.launch(SportsApp::class.java, *args)
        }
    }
}
