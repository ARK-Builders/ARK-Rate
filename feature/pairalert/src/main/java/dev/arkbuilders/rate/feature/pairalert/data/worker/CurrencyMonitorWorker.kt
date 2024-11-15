package dev.arkbuilders.rate.feature.pairalert.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.core.domain.model.TimestampType
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.feature.pairalert.presentation.utils.NotificationUtils

class CurrencyMonitorWorker(
    private val context: Context,
    params: WorkerParameters,
    private val handlePairAlertCheckUseCase: dev.arkbuilders.rate.feature.pairalert.domain.usecase.HandlePairAlertCheckUseCase,
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

    private fun notifyPair(pairAlert: dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert) {
        NotificationUtils.showPairAlert(pairAlert, context)
    }

    companion object {
        const val NAME = "CurrencyMonitorWorker"
    }
}
