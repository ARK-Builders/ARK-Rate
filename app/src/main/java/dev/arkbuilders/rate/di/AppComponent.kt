package dev.arkbuilders.rate.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.assets.AssetsRepo
import dev.arkbuilders.rate.data.worker.CurrencyMonitorWorker
import dev.arkbuilders.rate.di.module.ApiModule
import dev.arkbuilders.rate.di.module.DBModule
import dev.arkbuilders.rate.presentation.summary.SummaryViewModelFactory
import dev.arkbuilders.rate.presentation.addcurrency.AddCurrencyViewModelFactory
import dev.arkbuilders.rate.presentation.assets.AssetsViewModelFactory
import dev.arkbuilders.rate.presentation.shared.SharedViewModel
import dev.arkbuilders.rate.presentation.shared.SharedViewModelFactory
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class,
        DBModule::class
    ]
)
interface AppComponent {
    fun summaryViewModelFactory(): SummaryViewModelFactory
    fun assetsVMFactory(): AssetsViewModelFactory
    fun addCurrencyVMFactory(): AddCurrencyViewModelFactory
    fun sharedVMFactory(): SharedViewModelFactory

    fun generalCurrencyRepo(): GeneralCurrencyRepo
    fun assetsRepo(): AssetsRepo
    fun inject(currencyMonitorWorker: CurrencyMonitorWorker)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @BindsInstance context: Context
        ): AppComponent
    }
}