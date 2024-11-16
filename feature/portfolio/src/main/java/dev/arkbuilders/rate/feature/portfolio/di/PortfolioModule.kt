package dev.arkbuilders.rate.feature.portfolio.di

import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.db.dao.PortfolioDao
import dev.arkbuilders.rate.feature.portfolio.data.repo.PortfolioRepoImpl
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo

@Module
class PortfolioModule {
    @PortfolioScope
    @Provides
    fun portfolioRepo(portfolioDao: PortfolioDao): PortfolioRepo = PortfolioRepoImpl(portfolioDao)
}
