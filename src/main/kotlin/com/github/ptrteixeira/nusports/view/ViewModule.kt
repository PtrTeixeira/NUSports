/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.view

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

@Module
class ViewModule {
    @Provides
    @Named(VIEW_COROUTINE_POOL)
    internal fun providesCoroutineContext(): CoroutineContext = Dispatchers.Default

    companion object {
        const val VIEW_COROUTINE_POOL = "nusports.view.pool"
    }
}