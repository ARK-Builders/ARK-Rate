package dev.arkbuilders.ratewatch.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.arkbuilders.ratewatch.data.repo.CodeUseStatRepoImpl
import dev.arkbuilders.ratewatch.data.repo.PairAlertRepoImpl
import dev.arkbuilders.ratewatch.data.repo.PortfolioRepoImpl
import dev.arkbuilders.ratewatch.data.repo.QuickRepoImpl
import dev.arkbuilders.ratewatch.data.repo.TimestampRepoImpl
import dev.arkbuilders.ratewatch.data.repo.currency.CurrencyRepoImpl
import dev.arkbuilders.ratewatch.domain.repo.CodeUseStatRepo
import dev.arkbuilders.ratewatch.domain.repo.CurrencyRepo
import dev.arkbuilders.ratewatch.domain.repo.PairAlertRepo
import dev.arkbuilders.ratewatch.domain.repo.PortfolioRepo
import dev.arkbuilders.ratewatch.domain.repo.QuickRepo
import dev.arkbuilders.ratewatch.domain.repo.TimestampRepo

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {
    @Binds
    abstract fun currencyRepo(repo: CurrencyRepoImpl): CurrencyRepo

    @Binds
    abstract fun pairAlertRepo(repo: PairAlertRepoImpl): PairAlertRepo

    @Binds
    abstract fun portfolioRepo(repo: PortfolioRepoImpl): PortfolioRepo

    @Binds
    abstract fun quickRepo(repo: QuickRepoImpl): QuickRepo

    @Binds
    abstract fun codeUseStatRepo(codeUseStatRepoImpl: CodeUseStatRepoImpl): CodeUseStatRepo

    @Binds
    abstract fun timestampRepo(timestampRepoImpl: TimestampRepoImpl): TimestampRepo
}
