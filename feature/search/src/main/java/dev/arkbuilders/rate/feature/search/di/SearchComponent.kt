package dev.arkbuilders.rate.feature.search.di

import dagger.Component
import dev.arkbuilders.rate.core.di.CoreComponent
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.usecase.SearchUseCase
import dev.arkbuilders.rate.feature.search.presentation.SearchViewModelFactory

@SearchScope
@Component(dependencies = [CoreComponent::class])
interface SearchComponent {
    fun searchUseCase(): SearchUseCase

    fun analyticsManager(): AnalyticsManager

    fun searchVMFactory(): SearchViewModelFactory.Factory
}
