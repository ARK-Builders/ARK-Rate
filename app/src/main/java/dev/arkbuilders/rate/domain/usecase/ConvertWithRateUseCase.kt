package dev.arkbuilders.rate.domain.usecase

import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import javax.inject.Inject
import javax.inject.Singleton

// Example
// rate BTC = 67428 USD
// rate EUR = 1.07 USD
// rate BTC = 67428 / 1.07 = 63016 EUR
// 2 BTC to EUR = 2 * 63016 = 126032

@Singleton
class ConvertWithRateUseCase @Inject constructor(
    private val currencyRepo: CurrencyRepo
) {
    suspend operator fun invoke(
        fromCode: CurrencyCode,
        fromValue: Double,
        toCode: CurrencyCode,
        _rates: Map<CurrencyCode, CurrencyRate>? = null
    ): Pair<Amount, Double> {
        val rates = _rates ?: currencyRepo.getCodeToCurrencyRate().getOrNull()!!
        val toRate =
            rates[fromCode]!!.rate / rates[toCode]!!.rate

        return Amount(toCode, fromValue * toRate) to toRate
    }

    suspend operator fun invoke(
        from: Amount,
        toCode: CurrencyCode,
        _rates: Map<CurrencyCode, CurrencyRate>? = null
    ) = invoke(from.code, from.value, toCode, _rates)
}