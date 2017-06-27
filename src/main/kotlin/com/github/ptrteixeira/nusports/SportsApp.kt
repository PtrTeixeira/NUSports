/*
 * Copyright (c) 2017 Peter Teixeira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
            val component: ApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule())
                .build()

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
