package dev.arkbuilders.rate.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.db.PairAlertConditionRepo
import dev.arkbuilders.rate.data.model.CurrencyRate
import dev.arkbuilders.rate.data.model.PairAlertCondition
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.utils.NotificationUtils
import javax.inject.Inject

class CurrencyMonitorWorker(val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    @Inject
    lateinit var currencyRepo: GeneralCurrencyRepo

    @Inject
    lateinit var pairAlertRepo: PairAlertConditionRepo

    override suspend fun doWork(): Result {
        DIManager.component.inject(this)
        val rates = currencyRepo.getCurrencyRate()
        pairAlertRepo.getAll().forEach { pairAlert ->
            val curRatio = curRatio(pairAlert, rates)
            if (pairAlert.isConditionMet(curRatio)) {
                notifyPair(pairAlert, curRatio)
            }
        }

        return Result.success()
    }

    private fun curRatio(pairAlertCondition: PairAlertCondition, rates: List<CurrencyRate>): Float {
        val numeratorRate = rates.find { it.code == pairAlertCondition.numeratorCode }!!.rate
        val denominatorRate = rates.find { it.code == pairAlertCondition.denominatorCode }!!.rate
        return (numeratorRate / denominatorRate).toFloat()
    }


    private fun notifyPair(pairAlertCondition: PairAlertCondition, curRatio: Float) {
        NotificationUtils.showPairAlert(pairAlertCondition, curRatio, context)
    }

    companion object {
        const val name = "CurrencyMonitorWorker"
    }
}