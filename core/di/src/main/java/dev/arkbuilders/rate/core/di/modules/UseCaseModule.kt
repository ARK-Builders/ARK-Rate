package dev.arkbuilders.rate.core.di.modules

import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.DefaultGroupNameProvider
import dev.arkbuilders.rate.core.domain.usecase.GetGroupByIdOrCreateDefaultUseCase
import dev.arkbuilders.rate.core.domain.usecase.GetTopResultUseCase
import dev.arkbuilders.rate.core.domain.usecase.GroupReorderSwapUseCase
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

    @Singleton
    @Provides
    fun prepopulateDefaultGroupUseCase(
        groupRepo: GroupRepo,
        defaultGroupNameProvider: DefaultGroupNameProvider,
    ) = GetGroupByIdOrCreateDefaultUseCase(groupRepo, defaultGroupNameProvider)

    @Singleton
    @Provides
    fun groupReorderSwapUseCase(groupRepo: GroupRepo) = GroupReorderSwapUseCase(groupRepo)
}
