package dev.arkbuilders.rate.core.di.modules

import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.GetTopResultUseCase
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
    fun getTopResultUseCase(
        currencyRepo: CurrencyRepo,
        calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    ) = GetTopResultUseCase(currencyRepo, calcFrequentCurrUseCase)
}
