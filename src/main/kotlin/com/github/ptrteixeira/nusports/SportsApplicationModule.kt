package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.model.WebScraper
import com.github.ptrteixeira.nusports.presenter.MainController
import com.github.ptrteixeira.nusports.view.MainView
import dagger.Module
import dagger.Provides
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class SportsApplicationModule {
    @Provides
    fun provideView(controller: MainController): MainView {
        return MainView(controller)
    }

    @Provides
    fun provideController(executor: ExecutorService, webScraper: WebScraper): MainController {
        return MainController(executor, webScraper)
    }

    @Provides
    @Singleton
    fun provideExecutor(): ExecutorService {
        return ThreadPoolExecutor(2, 6,
                500, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue<Runnable>())
    }
}
