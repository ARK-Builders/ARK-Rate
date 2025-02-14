package dev.arkbuilders.rate.feature.portfolio.di

import dagger.Component
import dev.arkbuilders.rate.core.db.dao.PortfolioDao
import dev.arkbuilders.rate.core.di.CoreComponent
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.feature.portfolio.presentation.add.AddAssetViewModelFactory
import dev.arkbuilders.rate.feature.portfolio.presentation.edit.EditAssetViewModelFactory
import dev.arkbuilders.rate.feature.portfolio.presentation.main.PortfolioViewModelFactory

@PortfolioScope
@Component(dependencies = [CoreComponent::class], modules = [PortfolioModule::class])
interface PortfolioComponent {
    fun assetsVMFactory(): PortfolioViewModelFactory

    fun addCurrencyVMFactory(): AddAssetViewModelFactory.Factory

    fun editAssetVMFactory(): EditAssetViewModelFactory.Factory

    fun assetsRepo(): PortfolioRepo

    fun portfolioDao(): PortfolioDao
}
