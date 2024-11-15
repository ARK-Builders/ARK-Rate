package dev.arkbuilders.rate.feature.search.di

import dagger.Component
import dev.arkbuilders.rate.core.di.CoreComponent
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.usecase.GetTopResultUseCase
import dev.arkbuilders.rate.feature.search.presentation.SearchViewModelFactory

@SearchScope
@Component(dependencies = [CoreComponent::class])
interface SearchComponent {
    fun getTopResultUseCase(): GetTopResultUseCase
    fun analyticsManager(): AnalyticsManager
    fun searchVMFactory(): SearchViewModelFactory.Factory
}
