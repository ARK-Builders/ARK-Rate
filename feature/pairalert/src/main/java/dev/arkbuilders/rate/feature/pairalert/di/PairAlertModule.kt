package dev.arkbuilders.rate.feature.pairalert.di

import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.db.dao.PairAlertDao
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.feature.pairalert.data.repo.PairAlertRepoImpl
import dev.arkbuilders.rate.feature.pairalert.domain.repo.PairAlertRepo
import dev.arkbuilders.rate.feature.pairalert.domain.usecase.HandlePairAlertCheckUseCase

@Module
class PairAlertModule {
    @PairAlertScope
    @Provides
    fun pairAlertRepo(
        pairAlertDao: PairAlertDao,
        groupRepo: GroupRepo,
    ): PairAlertRepo = PairAlertRepoImpl(pairAlertDao, groupRepo)

    @PairAlertScope
    @Provides
    fun handlePairAlertCheckUseCase(
        currencyRepo: CurrencyRepo,
        pairAlertRepo: PairAlertRepo,
        convertWithRateUseCase: ConvertWithRateUseCase,
    ) = HandlePairAlertCheckUseCase(
        currencyRepo,
        pairAlertRepo,
        convertWithRateUseCase,
    )
}
