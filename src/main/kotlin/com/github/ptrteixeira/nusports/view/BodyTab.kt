/* Released under the MIT license, 2018 */

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
