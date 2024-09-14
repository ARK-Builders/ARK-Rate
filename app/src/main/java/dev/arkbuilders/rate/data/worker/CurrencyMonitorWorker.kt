package dev.arkbuilders.rate.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.domain.model.PairAlert
import dev.arkbuilders.rate.domain.model.TimestampType
import dev.arkbuilders.rate.domain.repo.TimestampRepo
import dev.arkbuilders.rate.domain.usecase.HandlePairAlertCheckUseCase
import dev.arkbuilders.rate.presentation.utils.NotificationUtils

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
        pairsToNotifyResult.getOrNull()!!.forEach { (pairAlert, currentRate) ->
            notifyPair(pairAlert, currentRate)
        }
        timestampRepo.rememberTimestamp(TimestampType.CheckPairAlerts)

        return Result.success()
    }

    private fun notifyPair(
        pairAlert: PairAlert,
        curRatio: Double,
    ) {
        NotificationUtils.showPairAlert(pairAlert, curRatio, context)
    }

    companion object {
        const val NAME = "CurrencyMonitorWorker"
    }
}
