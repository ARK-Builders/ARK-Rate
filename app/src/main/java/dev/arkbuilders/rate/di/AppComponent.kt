package dev.arkbuilders.rate.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.arkbuilders.rate.data.currency.CurrencyRepoImpl
import dev.arkbuilders.rate.data.db.PortfolioRepoImpl
import dev.arkbuilders.rate.data.preferences.PrefsImpl
import dev.arkbuilders.rate.data.worker.AppWorkerFactory
import dev.arkbuilders.rate.data.worker.CurrencyMonitorWorker
import dev.arkbuilders.rate.di.module.ApiModule
import dev.arkbuilders.rate.di.module.DBModule
import dev.arkbuilders.rate.di.module.RepoModule
import dev.arkbuilders.rate.domain.repo.Prefs
import dev.arkbuilders.rate.presentation.addcurrency.AddCurrencyViewModelFactory
import dev.arkbuilders.rate.presentation.portfolio.PortfolioViewModelFactory
import dev.arkbuilders.rate.presentation.pairalert.AddPairAlertViewModelFactory
import dev.arkbuilders.rate.presentation.pairalert.PairAlertViewModelFactory
import dev.arkbuilders.rate.presentation.portfolio.EditAssetViewModelFactory
import dev.arkbuilders.rate.presentation.quick.AddQuickViewModelFactory
import dev.arkbuilders.rate.presentation.quick.QuickViewModelFactory
import dev.arkbuilders.rate.presentation.settings.SettingsViewModelFactory
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class,
        DBModule::class,
        RepoModule::class
    ]
)
interface AppComponent {
    fun assetsVMFactory(): PortfolioViewModelFactory
    fun addCurrencyVMFactory(): AddCurrencyViewModelFactory
    fun addQuickVMFactory(): AddQuickViewModelFactory
    fun pairAlertVMFactory(): PairAlertViewModelFactory
    fun addPairAlertVMFactory(): AddPairAlertViewModelFactory
    fun quickVMFactory(): QuickViewModelFactory.Factory
    fun editAssetVMFactory(): EditAssetViewModelFactory.Factory
    fun settingsVMFactory(): SettingsViewModelFactory
    fun appWorkerFactory(): AppWorkerFactory

    fun prefs(): Prefs

    fun generalCurrencyRepo(): CurrencyRepoImpl
    fun assetsRepo(): PortfolioRepoImpl
    fun inject(currencyMonitorWorker: CurrencyMonitorWorker)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @BindsInstance context: Context
        ): AppComponent
    }
}