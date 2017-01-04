package com.github.ptrteixeira.nusports.model

import dagger.Module
import dagger.Provides
import javafx.collections.ObservableList
import java.util.*

@Module
class ApplicationModelModule {
    @Provides
    internal fun provideWebScraper(nuWebScraper: NUWebScraper): WebScraper {
        return nuWebScraper
    }

    @Provides
    internal fun provideStandingsCache(): Map<String, ObservableList<Standing>> {
        return HashMap()
    }

    @Provides
    internal fun provideScheduleCache(): Map<String, ObservableList<Match>> {
        return HashMap()
    }

    @Provides
    internal fun provideDocumentSource(nuDocumentSource: NUDocumentSource): DocumentSource {
        return nuDocumentSource
    }
}
