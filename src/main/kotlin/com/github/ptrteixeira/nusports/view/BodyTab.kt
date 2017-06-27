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
package com.github.ptrteixeira.nusports.view

import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import javafx.scene.paint.Color
import tornadofx.View
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.center
import tornadofx.combobox
import tornadofx.left
import tornadofx.progressbar
import tornadofx.text
import tornadofx.vbox
import tornadofx.visibleWhen

class BodyTab(
    private val sports: List<String>,
    private val centerContents: View,
    selectedItem: StringProperty,
    errorText: StringProperty,
    isLoading: BooleanProperty
) : View() {
    override val root = borderpane {
        left {
            vbox {
                alignment = javafx.geometry.Pos.TOP_CENTER

                combobox<String>(selectedItem, sports)

                text(errorText) {
                    fill = Color.RED
                    styleClass += "error"
                }
            }
        }

        center {
            add(centerContents)
        }

        bottom {
            progressbar {
                visibleWhen(isLoading)
            }
        }
    }
}
