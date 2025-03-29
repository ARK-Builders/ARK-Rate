package dev.arkbuilders.rate.feature.pairalert.domain.usecase

import dev.arkbuilders.rate.core.domain.divideArk
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyRate
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert
import dev.arkbuilders.rate.feature.pairalert.domain.repo.PairAlertRepo
import java.math.BigDecimal
import java.time.OffsetDateTime

class HandlePairAlertCheckUseCase(
    private val currencyRepo: CurrencyRepo,
    private val pairAlertRepo: PairAlertRepo,
    private val convertUseCase: ConvertWithRateUseCase,
) {
    // PairAlert to current rate
    suspend operator fun invoke(): List<Pair<PairAlert, BigDecimal>> {
        val rates = currencyRepo.getCodeToCurrencyRate()
        val pairsToNotify =
            pairAlertRepo.getAll()
                .filter { it.enabled }
                .mapNotNull { pairAlert ->
                    val (met, currentRate) = isConditionMet(rates, pairAlert)
                    if (met) {
                        if (pairAlert.oneTimeNotRecurrent) {
                            handleOneTimePair(pairAlert)
                        } else {
                            handleRecurrentPair(pairAlert)
                        }

                        return@mapNotNull pairAlert to currentRate
                    }

                    return@mapNotNull null
                }

        return pairsToNotify
    }

    private suspend fun handleOneTimePair(pairAlert: PairAlert) {
        pairAlertRepo.insert(
            pairAlert.copy(
                enabled = false,
                lastDateTriggered = OffsetDateTime.now(),
            ),
        )
    }

    private suspend fun handleRecurrentPair(pairAlert: PairAlert) {
        val updatedTargetPrice =
            pairAlert.percent?.let { percent ->
                val percentFactor =
                    BigDecimal.ONE +
                        BigDecimal.valueOf(percent).divideArk(BigDecimal.valueOf(100))
                pairAlert.targetPrice * percentFactor
            } ?: let {
                val diff = (pairAlert.targetPrice - pairAlert.startPrice)
                pairAlert.targetPrice + diff
            }
        val updatedPair =
            pairAlert.copy(
                startPrice = pairAlert.targetPrice,
                targetPrice = updatedTargetPrice,
                lastDateTriggered = OffsetDateTime.now(),
            )
        pairAlertRepo.insert(updatedPair)
    }

    private suspend fun isConditionMet(
        rates: Map<CurrencyCode, CurrencyRate>,
        pairAlert: PairAlert,
    ): Pair<Boolean, BigDecimal> {
        val (_, rate) =
            convertUseCase.invoke(
                Amount(pairAlert.baseCode, BigDecimal.ONE),
                pairAlert.targetCode,
                rates,
            )
        var result = false
        if (pairAlert.targetPrice > pairAlert.startPrice) {
            if (rate >= pairAlert.targetPrice) {
                result = true
            }
        } else {
            if (rate <= pairAlert.targetPrice) {
                result = true
            }
        }
        return result to rate
    }
}
