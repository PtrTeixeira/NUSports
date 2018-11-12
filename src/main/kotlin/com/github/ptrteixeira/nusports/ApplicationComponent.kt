/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.model.ApplicationModelModule
import com.github.ptrteixeira.nusports.presenter.ViewState
import com.github.ptrteixeira.nusports.view.ViewModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ApplicationModelModule::class, ViewModule::class])
@Singleton
interface ApplicationComponent {
    fun viewState(): ViewState
}
