/* Released under the MIT license, $YEAR */

package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.model.ApplicationModelModule
import com.github.ptrteixeira.nusports.presenter.ViewState
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(ApplicationModule::class, ApplicationModelModule::class))
@Singleton
interface ApplicationComponent {
    fun viewState(): ViewState
}
