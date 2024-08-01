package dev.arkbuilders.rate.domain.usecase

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.model.PairAlert
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PairAlertRepo
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandlePairAlertCheckUseCase @Inject constructor(
    private val currencyRepo: CurrencyRepo,
    private val pairAlertRepo: PairAlertRepo,
    private val convertUseCase: ConvertWithRateUseCase
) {
    // PairAlert to current rate
    suspend operator fun invoke(): Either<Throwable, List<Pair<PairAlert, Double>>> {
        val rates = currencyRepo.getCodeToCurrencyRate().getOrElse {
            return it.left()
        }
        val pairsToNotify = pairAlertRepo.getAll()
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

        return pairsToNotify.right()
    }

    private suspend fun handleOneTimePair(pairAlert: PairAlert) {
        pairAlertRepo.insert(
            pairAlert.copy(
                enabled = false,
                lastDateTriggered = OffsetDateTime.now()
            )
        )
    }

    private suspend fun handleRecurrentPair(pairAlert: PairAlert) {
        val updatedTargetPrice = pairAlert.percent?.let { percent ->
            (1 + percent / 100) * pairAlert.targetPrice
        } ?: let {
            val diff = (pairAlert.targetPrice - pairAlert.startPrice)
            pairAlert.targetPrice + diff
        }
        val updatedPair = pairAlert.copy(
            startPrice = pairAlert.targetPrice,
            targetPrice = updatedTargetPrice,
            lastDateTriggered = OffsetDateTime.now()
        )
        pairAlertRepo.insert(updatedPair)
    }

    private suspend fun isConditionMet(
        rates: Map<CurrencyCode, CurrencyRate>,
        pairAlert: PairAlert
    ): Pair<Boolean, Double> {
        val (_, rate) = convertUseCase.invoke(
            Amount(pairAlert.baseCode, 1.0),
            pairAlert.targetCode,
            rates
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