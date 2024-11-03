package dev.arkbuilders.rate.core.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.core.domain.model.PairAlert
import dev.arkbuilders.rate.core.domain.model.TimestampType
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.core.domain.usecase.HandlePairAlertCheckUseCase

class CurrencyMonitorWorker(
    private val context: Context,
    params: WorkerParameters,
    private val handlePairAlertCheckUseCase: HandlePairAlertCheckUseCase,
    private val timestampRepo: TimestampRepo,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val pairsToNotifyResult = handlePairAlertCheckUseCase()
        pairsToNotifyResult.onLeft {
            return Result.failure()
        }
        pairsToNotifyResult.getOrNull()!!.forEach { (pairAlert, _) ->
            notifyPair(pairAlert)
        }
        timestampRepo.rememberTimestamp(TimestampType.CheckPairAlerts)

        return Result.success()
    }

    private fun notifyPair(pairAlert: PairAlert) {
//        NotificationUtils.showPairAlert(pairAlert, context)
    }

    companion object {
        const val NAME = "CurrencyMonitorWorker"
    }
}
