package dev.arkbuilders.rate.core.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.arkbuilders.rate.core.db.dao.PairAlertDao
import dev.arkbuilders.rate.core.db.dao.PortfolioDao
import dev.arkbuilders.rate.core.db.dao.QuickPairDao
import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.NetworkStatus
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.core.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.GetTopResultUseCase
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoreDataModule::class,
    ],
)
interface CoreComponent {
    fun quickDao(): QuickPairDao

    fun portfolioDao(): PortfolioDao

    fun pairAlertDao(): PairAlertDao

    fun codesUseStatRepo(): CodeUseStatRepo

    fun appContext(): Context

    fun buildConfigFieldsProvider(): BuildConfigFieldsProvider

    fun currencyRepo(): CurrencyRepo

    fun timestampRepo(): TimestampRepo

    fun prefs(): Prefs

    fun networkStatus(): NetworkStatus

    fun convertUseCase(): ConvertWithRateUseCase

    fun calcFrequentCurrUseCase(): CalcFrequentCurrUseCase

    fun getTopResultUseCase(): GetTopResultUseCase

    fun analyticsManager(): AnalyticsManager

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @BindsInstance context: Context,
        ): CoreComponent
    }
}