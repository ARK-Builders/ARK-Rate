package space.taran.arkrate.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import space.taran.arkrate.data.GeneralCurrencyRepo
import space.taran.arkrate.data.assets.AssetsRepo
import space.taran.arkrate.data.worker.CurrencyMonitorWorker
import space.taran.arkrate.di.module.ApiModule
import space.taran.arkrate.di.module.DBModule
import space.taran.arkrate.presentation.summary.SummaryViewModelFactory
import space.taran.arkrate.presentation.addcurrency.AddCurrencyViewModelFactory
import space.taran.arkrate.presentation.assets.AssetsViewModelFactory
import space.taran.arkrate.presentation.shared.SharedViewModel
import space.taran.arkrate.presentation.shared.SharedViewModelFactory
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