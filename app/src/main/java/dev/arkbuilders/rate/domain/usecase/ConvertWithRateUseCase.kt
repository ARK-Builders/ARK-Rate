package dev.arkbuilders.rate.domain.usecase

import dev.arkbuilders.rate.data.divideArk
import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

// Example
// rate BTC = 67428 USD
// rate EUR = 1.07 USD
// rate BTC = 67428 / 1.07 = 63016 EUR
// 2 BTC to EUR = 2 * 63016 = 126032

@Singleton
class ConvertWithRateUseCase @Inject constructor(
    private val currencyRepo: CurrencyRepo,
) {
    suspend operator fun invoke(
        fromCode: CurrencyCode,
        fromValue: BigDecimal = BigDecimal.ONE,
        toCode: CurrencyCode,
        _rates: Map<CurrencyCode, CurrencyRate>? = null,
    ): Pair<Amount, BigDecimal> {
        val rates = _rates ?: currencyRepo.getCodeToCurrencyRate().getOrNull()!!
        val fromRate = rates[fromCode]!!.rate
        val toRate = rates[toCode]!!.rate
        val rate = fromRate.divideArk(toRate)

        return Amount(toCode, fromValue * rate) to rate
    }

    suspend operator fun invoke(
        from: Amount,
        toCode: CurrencyCode,
        _rates: Map<CurrencyCode, CurrencyRate>? = null,
    ) = invoke(from.code, from.value, toCode, _rates)
}
