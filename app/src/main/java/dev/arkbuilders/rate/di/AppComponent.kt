package dev.arkbuilders.rate.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.arkbuilders.rate.data.repo.PortfolioRepoImpl
import dev.arkbuilders.rate.data.repo.QuickRepoImpl
import dev.arkbuilders.rate.data.repo.currency.CurrencyRepoImpl
import dev.arkbuilders.rate.data.worker.AppWorkerFactory
import dev.arkbuilders.rate.data.worker.CurrencyMonitorWorker
import dev.arkbuilders.rate.di.module.ApiModule
import dev.arkbuilders.rate.di.module.DBModule
import dev.arkbuilders.rate.di.module.RepoModule
import dev.arkbuilders.rate.domain.repo.NetworkStatus
import dev.arkbuilders.rate.domain.repo.Prefs
import dev.arkbuilders.rate.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.domain.usecase.GetSortedPinnedQuickPairsUseCase
import dev.arkbuilders.rate.presentation.pairalert.AddPairAlertViewModelFactory
import dev.arkbuilders.rate.presentation.pairalert.PairAlertViewModelFactory
import dev.arkbuilders.rate.presentation.portfolio.AddAssetViewModelFactory
import dev.arkbuilders.rate.presentation.portfolio.EditAssetViewModelFactory
import dev.arkbuilders.rate.presentation.portfolio.PortfolioViewModelFactory
import dev.arkbuilders.rate.presentation.quick.AddQuickViewModelFactory
import dev.arkbuilders.rate.presentation.quick.QuickViewModelFactory
import dev.arkbuilders.rate.presentation.search.SearchViewModelFactory
import dev.arkbuilders.rate.presentation.settings.SettingsViewModelFactory
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class,
        DBModule::class,
        RepoModule::class,
    ],
)
interface AppComponent {
    fun assetsVMFactory(): PortfolioViewModelFactory

    fun addCurrencyVMFactory(): AddAssetViewModelFactory

    fun addQuickVMFactory(): AddQuickViewModelFactory.Factory

    fun pairAlertVMFactory(): PairAlertViewModelFactory

    fun addPairAlertVMFactory(): AddPairAlertViewModelFactory.Factory

    fun quickVMFactory(): QuickViewModelFactory.Factory

    fun editAssetVMFactory(): EditAssetViewModelFactory.Factory

    fun searchVMFactory(): SearchViewModelFactory.Factory

    fun settingsVMFactory(): SettingsViewModelFactory

    fun appWorkerFactory(): AppWorkerFactory

    fun prefs(): Prefs

    fun networkStatus(): NetworkStatus

    fun generalCurrencyRepo(): CurrencyRepoImpl

    fun assetsRepo(): PortfolioRepoImpl

    fun quickRepo(): QuickRepoImpl

    fun convertUseCase(): ConvertWithRateUseCase

    fun inject(currencyMonitorWorker: CurrencyMonitorWorker)

    fun calcFrequentCurrUseCase(): CalcFrequentCurrUseCase

    fun getSortedPinnedQuickPairsUseCase(): GetSortedPinnedQuickPairsUseCase

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @BindsInstance context: Context,
        ): AppComponent
    }
}
