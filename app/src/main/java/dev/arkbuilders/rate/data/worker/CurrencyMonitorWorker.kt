package dev.arkbuilders.rate.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.db.PairAlertRepo
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.CurrencyRate
import dev.arkbuilders.rate.data.model.PairAlert
import dev.arkbuilders.rate.presentation.utils.NotificationUtils
import java.time.OffsetDateTime

class CurrencyMonitorWorker(
    private val context: Context,
    params: WorkerParameters,
    private val currencyRepo: GeneralCurrencyRepo,
    private val pairAlertRepo: PairAlertRepo
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val rates = currencyRepo.getCodeToCurrencyRate()
        pairAlertRepo.getAll().forEach { pairAlert ->
            val (met, currentRate) = isConditionMet(rates, pairAlert)
            if (met) {
                if (!pairAlert.oneTimeNotRecurrent) {
                    pairAlertRepo.insert(
                        pairAlert.copy(
                            triggered = true,
                            enabled = false,
                            lastDateTriggered = OffsetDateTime.now()
                        )
                    )
                } else {
                    updatePairAndSave(pairAlert)
                }
                notifyPair(pairAlert, currentRate)
            }

        }

        return Result.success()
    }

    private suspend fun updatePairAndSave(pairAlert: PairAlert) {
        val updatedTargetPrice = pairAlert.alertPercent?.let { percent ->
            pairAlert.targetPrice + (pairAlert.targetPrice * percent)
        } ?: let {
            pairAlert.targetPrice + (pairAlert.targetPrice - pairAlert.startPrice)
        }
        val updatedPair = pairAlert.copy(
            triggered = true,
            startPrice = pairAlert.targetPrice,
            targetPrice = updatedTargetPrice,
            lastDateTriggered = OffsetDateTime.now()
        )
        pairAlertRepo.insert(updatedPair)
    }

    // current BTC = 46000 USD
    // current EUR = 1.1 USD
    // target BTC = 40000 EUR
    // current BTC in EUR 46000 / 1.1
    private fun isConditionMet(
        rates: Map<CurrencyCode, CurrencyRate>,
        pairAlert: PairAlert
    ): Pair<Boolean, Double> {
        val current =
            rates[pairAlert.targetCode]!!.rate / rates[pairAlert.baseCode]!!.rate
        var result = false
        if (pairAlert.targetPrice > pairAlert.startPrice) {
            if (current >= pairAlert.targetPrice) {
                result = true
            }
        } else {
            if (current <= pairAlert.targetPrice) {
                result = true
            }
        }
        return result to current
    }


    private fun notifyPair(pairAlert: PairAlert, curRatio: Double) {
        NotificationUtils.showPairAlert(pairAlert, curRatio, context)
    }

    companion object {
        const val name = "CurrencyMonitorWorker"
    }
}