package dev.arkbuilders.rate.feature.quick.di

import dagger.Component
import dev.arkbuilders.rate.core.di.CoreComponent
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import dev.arkbuilders.rate.feature.quick.domain.usecase.GetSortedPinnedQuickPairsUseCase
import dev.arkbuilders.rate.feature.quick.presentation.add.AddQuickViewModelFactory
import dev.arkbuilders.rate.feature.quick.presentation.main.QuickViewModelFactory

@QuickScope
@Component(dependencies = [CoreComponent::class], modules = [QuickModule::class])
interface QuickComponent {
    fun addQuickVMFactory(): AddQuickViewModelFactory.Factory
    fun quickVMFactory(): QuickViewModelFactory.Factory
    fun getPinnedQuickPairUseCase(): GetSortedPinnedQuickPairsUseCase
    fun quickRepo(): QuickRepo
}
