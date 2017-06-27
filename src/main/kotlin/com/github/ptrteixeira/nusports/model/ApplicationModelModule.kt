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
package com.github.ptrteixeira.nusports.model

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.CommonPool
import java.util.HashMap
import javax.inject.Named
import kotlin.coroutines.experimental.CoroutineContext

@Module
class ApplicationModelModule {
    @Provides
    internal fun provideWebScraper(nuWebScraper: NuWebScraper): WebScraper = nuWebScraper

    @Provides
    @Named(MODEL_COROUTINE_POOL)
    internal fun providesCoroutineContext(): CoroutineContext = CommonPool

    @Provides
    internal fun provideStandingsCache(): Map<String, List<Standing>> {
        return HashMap()
    }

    @Provides
    internal fun provideScheduleCache(): Map<String, List<Match>> {
        return HashMap()
    }

    @Provides
    internal fun provideDocumentSource(nuDocumentSource: NuDocumentSource): DocumentSource = nuDocumentSource

    companion object {
        const val MODEL_COROUTINE_POOL = "nusports.model.pool"
    }
}
