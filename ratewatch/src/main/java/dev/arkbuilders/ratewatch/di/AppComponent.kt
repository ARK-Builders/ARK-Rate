package dev.arkbuilders.ratewatch.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.arkbuilders.ratewatch.data.repo.PortfolioRepoImpl
import dev.arkbuilders.ratewatch.data.repo.QuickRepoImpl
import dev.arkbuilders.ratewatch.data.repo.currency.CurrencyRepoImpl
import dev.arkbuilders.ratewatch.di.module.ApiModule
import dev.arkbuilders.ratewatch.di.module.DBModule
import dev.arkbuilders.ratewatch.di.module.RepoModule
import dev.arkbuilders.ratewatch.domain.repo.NetworkStatus
import dev.arkbuilders.ratewatch.domain.repo.Prefs
import dev.arkbuilders.ratewatch.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.ratewatch.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.ratewatch.domain.usecase.GetSortedPinnedQuickPairsUseCase
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
    fun prefs(): Prefs

    fun networkStatus(): NetworkStatus

    fun generalCurrencyRepo(): CurrencyRepoImpl

    fun assetsRepo(): PortfolioRepoImpl

    fun quickRepo(): QuickRepoImpl

    fun convertUseCase(): ConvertWithRateUseCase

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
