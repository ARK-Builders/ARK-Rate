package dev.arkbuilders.rate.di.module

import dagger.Binds
import dagger.Module
import dev.arkbuilders.rate.data.network.NetworkStatusImpl
import dev.arkbuilders.rate.data.preferences.PrefsImpl
import dev.arkbuilders.rate.data.repo.AnalyticsManagerImpl
import dev.arkbuilders.rate.data.repo.CodeUseStatRepoImpl
import dev.arkbuilders.rate.data.repo.PairAlertRepoImpl
import dev.arkbuilders.rate.data.repo.PortfolioRepoImpl
import dev.arkbuilders.rate.data.repo.QuickRepoImpl
import dev.arkbuilders.rate.data.repo.TimestampRepoImpl
import dev.arkbuilders.rate.data.repo.currency.CurrencyRepoImpl
import dev.arkbuilders.rate.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.NetworkStatus
import dev.arkbuilders.rate.domain.repo.PairAlertRepo
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.domain.repo.Prefs
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.repo.TimestampRepo

@Module
abstract class RepoModule {
    @Binds
    abstract fun currencyRepo(repo: CurrencyRepoImpl): CurrencyRepo

    @Binds
    abstract fun pairAlertRepo(repo: PairAlertRepoImpl): PairAlertRepo

    @Binds
    abstract fun portfolioRepo(repo: PortfolioRepoImpl): PortfolioRepo

    @Binds
    abstract fun quickRepo(repo: QuickRepoImpl): QuickRepo

    @Binds
    abstract fun prefs(prefs: PrefsImpl): Prefs

    @Binds
    abstract fun codeUseStatRepo(codeUseStatRepoImpl: CodeUseStatRepoImpl): CodeUseStatRepo

    @Binds
    abstract fun analyticsManager(analyticsManagerImpl: AnalyticsManagerImpl): AnalyticsManager

    @Binds
    abstract fun timestampRepo(timestampRepoImpl: TimestampRepoImpl): TimestampRepo

    @Binds
    abstract fun networkStatus(networkStatusImpl: NetworkStatusImpl): NetworkStatus
}
