package dev.arkbuilders.rate.feature.quickwidget.di

import dagger.Component
import dev.arkbuilders.rate.feature.quick.di.QuickComponent
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import dev.arkbuilders.rate.feature.quick.domain.usecase.GetSortedPinnedQuickPairsUseCase

@QuickWidgetScope
@Component(dependencies = [QuickComponent::class])
interface QuickWidgetComponent {
    fun quickRepo(): QuickRepo

    fun getPinnedQuickPairUseCase(): GetSortedPinnedQuickPairsUseCase
}
