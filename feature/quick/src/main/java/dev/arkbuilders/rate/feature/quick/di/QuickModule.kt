package dev.arkbuilders.rate.feature.quick.di

import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.db.dao.QuickCalculationDao
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.feature.quick.data.QuickRepoImpl
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import dev.arkbuilders.rate.feature.quick.domain.usecase.GetSortedPinnedQuickCalculationsUseCase

@Module
class QuickModule {
    @QuickScope
    @Provides
    fun quickRepo(
        quickPairDao: QuickCalculationDao,
        groupRepo: GroupRepo,
    ): QuickRepo = QuickRepoImpl(quickPairDao, groupRepo)

    @QuickScope
    @Provides
    fun getSortedPinnedQuickPairsUseCase(
        quickRepo: QuickRepo,
        convertWithRateUseCase: ConvertWithRateUseCase,
    ) = GetSortedPinnedQuickCalculationsUseCase(
        quickRepo,
        convertWithRateUseCase,
    )
}
