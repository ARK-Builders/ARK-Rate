package dev.arkbuilders.rate.core.domain.usecase

import dev.arkbuilders.rate.core.domain.model.CurrencyName
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo

class GetTopResultUseCase(
    private val currencyRepo: CurrencyRepo,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
) {
    suspend operator fun invoke(): List<CurrencyName> {
        val allCurrencies = currencyRepo.getCurrencyNameUnsafe()
        val frequent =
            calcFrequentCurrUseCase.invoke()
                .map { currencyRepo.nameByCodeUnsafe(it) }
        return frequent + (allCurrencies - frequent)
    }
}
