package dev.arkbuilders.rate.feature.portfolio.di

import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.db.dao.PortfolioDao
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.feature.portfolio.data.repo.PortfolioRepoImpl
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.feature.portfolio.domain.usecase.AddNewAssetsUseCase

@Module
class PortfolioModule {
    @PortfolioScope
    @Provides
    fun portfolioRepo(
        portfolioDao: PortfolioDao,
        groupRepo: GroupRepo,
    ): PortfolioRepo = PortfolioRepoImpl(portfolioDao, groupRepo)

    @PortfolioScope
    @Provides
    fun addNewAssetsUseCase(portfolioRepo: PortfolioRepo): AddNewAssetsUseCase =
        AddNewAssetsUseCase(portfolioRepo)
}
