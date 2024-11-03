package dev.arkbuilders.rate.core.data.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.data.db.dao.CodeUseStatDao
import dev.arkbuilders.rate.core.data.db.dao.PairAlertDao
import dev.arkbuilders.rate.core.data.db.dao.PortfolioDao
import dev.arkbuilders.rate.core.data.db.dao.QuickPairDao
import dev.arkbuilders.rate.core.data.db.dao.TimestampDao
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.NetworkStatus
import dev.arkbuilders.rate.core.domain.repo.PairAlertRepo
import dev.arkbuilders.rate.core.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.core.domain.repo.QuickRepo
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.core.data.network.NetworkStatusImpl
import dev.arkbuilders.rate.core.data.preferences.PrefsImpl
import dev.arkbuilders.rate.core.data.repo.AnalyticsManagerImpl
import dev.arkbuilders.rate.core.data.repo.CodeUseStatRepoImpl
import dev.arkbuilders.rate.core.data.repo.PairAlertRepoImpl
import dev.arkbuilders.rate.core.data.repo.PortfolioRepoImpl
import dev.arkbuilders.rate.core.data.repo.QuickRepoImpl
import dev.arkbuilders.rate.core.data.repo.TimestampRepoImpl
import dev.arkbuilders.rate.core.data.repo.currency.CryptoCurrencyDataSource
import dev.arkbuilders.rate.core.data.repo.currency.CurrencyRepoImpl
import dev.arkbuilders.rate.core.data.repo.currency.FiatCurrencyDataSource
import dev.arkbuilders.rate.core.data.repo.currency.LocalCurrencyDataSource
import javax.inject.Singleton

@Module
class RepoModule {

    @Singleton
    @Provides
    fun currencyRepo(
        fiatCurrencyDataSource: FiatCurrencyDataSource,
        cryptoCurrencyDataSource: CryptoCurrencyDataSource,
        localCurrencyDataSource: LocalCurrencyDataSource,
        timestampRepo: TimestampRepo,
        networkStatus: NetworkStatus
    ): CurrencyRepo = CurrencyRepoImpl(
        fiatCurrencyDataSource,
        cryptoCurrencyDataSource,
        localCurrencyDataSource,
        timestampRepo,
        networkStatus
    )

    @Singleton
    @Provides
    fun pairAlertRepo(pairAlertDao: PairAlertDao): PairAlertRepo = PairAlertRepoImpl(pairAlertDao)

    @Singleton
    @Provides
    fun portfolioRepo(portfolioDao: PortfolioDao): PortfolioRepo = PortfolioRepoImpl(portfolioDao)

    @Singleton
    @Provides
    fun quickRepo(quickPairDao: QuickPairDao): QuickRepo = QuickRepoImpl(quickPairDao)

    @Singleton
    @Provides
    fun prefs(context: Context): Prefs = PrefsImpl(context)

    @Singleton
    @Provides
    fun codeUseStatRepo(
        codeUseStatDao: CodeUseStatDao
    ): CodeUseStatRepo = CodeUseStatRepoImpl(codeUseStatDao)

    @Singleton
    @Provides
    fun analyticsManager(prefs: Prefs): AnalyticsManager = AnalyticsManagerImpl(prefs)

    @Singleton
    @Provides
    fun timestampRepo(timestampDao: TimestampDao): TimestampRepo = TimestampRepoImpl(timestampDao)

    @Singleton
    @Provides
    fun networkStatus(context: Context): NetworkStatus = NetworkStatusImpl(context)
}
