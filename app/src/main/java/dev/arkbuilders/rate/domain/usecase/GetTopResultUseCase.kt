package dev.arkbuilders.rate.domain.usecase

import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTopResultUseCase @Inject constructor(
    private val currencyRepo: CurrencyRepo,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase
) {
    suspend operator fun invoke(): List<CurrencyName> {
        val allCurrencies = currencyRepo.getCurrencyNameUnsafe()
        val frequent = calcFrequentCurrUseCase.invoke()
            .map { currencyRepo.nameByCodeUnsafe(it) }
        return frequent + (allCurrencies - frequent)
    }
}
