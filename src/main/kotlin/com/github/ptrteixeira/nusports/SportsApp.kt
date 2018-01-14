/* Released under the MIT license, $YEAR */

package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.presenter.ViewState
import com.github.ptrteixeira.nusports.view.Body
import com.github.ptrteixeira.nusports.view.SportsWorkspace
import javafx.application.Application
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
        @JvmStatic
        fun main(args: Array<String>) {
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
