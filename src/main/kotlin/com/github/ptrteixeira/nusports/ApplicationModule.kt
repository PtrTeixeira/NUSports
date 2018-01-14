/* Released under the MIT license, $YEAR */

package com.github.ptrteixeira.nusports

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.javafx.JavaFx
import javax.inject.Named
import kotlin.coroutines.experimental.CoroutineContext

@Module
class ApplicationModule {
    @Provides
    @Named(UI_COROUTINE_POOL)
    internal fun providesContext(): CoroutineContext = JavaFx

    companion object {
        const val UI_COROUTINE_POOL = "nusports.ui.pool"
    }
}
