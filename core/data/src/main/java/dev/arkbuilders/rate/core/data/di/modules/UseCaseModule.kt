package dev.arkbuilders.rate.core.data.di.modules

import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.PairAlertRepo
import dev.arkbuilders.rate.core.domain.repo.QuickRepo
import dev.arkbuilders.rate.core.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.GetSortedPinnedQuickPairsUseCase
import dev.arkbuilders.rate.core.domain.usecase.GetTopResultUseCase
import dev.arkbuilders.rate.core.domain.usecase.HandlePairAlertCheckUseCase
import javax.inject.Singleton

@Module
class UseCaseModule {
    @Singleton
    @Provides
    fun calcFrequentCurrUseCase(codeUseStatRepo: CodeUseStatRepo) =
        CalcFrequentCurrUseCase(codeUseStatRepo)

    @Singleton
    @Provides
    fun convertWithRateUseCase(currencyRepo: CurrencyRepo) = ConvertWithRateUseCase(currencyRepo)

    @Singleton
    @Provides
    fun getSortedPinnedQuickPairsUseCase(
        quickRepo: QuickRepo,
        convertWithRateUseCase: ConvertWithRateUseCase
    ) = GetSortedPinnedQuickPairsUseCase(quickRepo, convertWithRateUseCase)

    @Singleton
    @Provides
    fun getTopResultUseCase(
        currencyRepo: CurrencyRepo,
        calcFrequentCurrUseCase: CalcFrequentCurrUseCase
    ) = GetTopResultUseCase(currencyRepo, calcFrequentCurrUseCase)

    @Singleton
    @Provides
    fun handlePairAlertCheckUseCase(
        currencyRepo: CurrencyRepo,
        pairAlertRepo: PairAlertRepo,
        convertWithRateUseCase: ConvertWithRateUseCase
    ) = HandlePairAlertCheckUseCase(currencyRepo, pairAlertRepo, convertWithRateUseCase)
}
