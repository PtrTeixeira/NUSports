package com.github.ptrteixeira.nusports

import com.github.ptrteixeira.nusports.model.ApplicationModelModule
import com.github.ptrteixeira.nusports.view.MainView
import dagger.Component
import java.util.concurrent.ExecutorService
import javax.inject.Singleton

@Component(modules = arrayOf(SportsApplicationModule::class, ApplicationModelModule::class))
@Singleton
interface SportsApplication {
    fun mainView(): MainView
    fun executor(): ExecutorService
}
