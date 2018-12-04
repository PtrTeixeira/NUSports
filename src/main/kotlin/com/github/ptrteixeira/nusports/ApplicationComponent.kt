/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.presenter.ViewState
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ApplicationModule::class])
@Singleton
interface ApplicationComponent {
    fun viewState(): ViewState
}
