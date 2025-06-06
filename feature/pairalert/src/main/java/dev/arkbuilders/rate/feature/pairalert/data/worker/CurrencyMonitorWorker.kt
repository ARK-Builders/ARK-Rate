package dev.arkbuilders.rate.feature.pairalert.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.core.domain.model.TimestampType
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert
import dev.arkbuilders.rate.feature.pairalert.domain.usecase.HandlePairAlertCheckUseCase
import dev.arkbuilders.rate.feature.pairalert.presentation.utils.NotificationUtils

class CurrencyMonitorWorker(
    private val context: Context,
    params: WorkerParameters,
    private val handlePairAlertCheckUseCase: HandlePairAlertCheckUseCase,
    private val timestampRepo: TimestampRepo,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val pairsToNotify = handlePairAlertCheckUseCase()
        pairsToNotify.forEach { (pairAlert, _) ->
            notifyPair(pairAlert)
        }
        timestampRepo.rememberTimestamp(TimestampType.CheckPairAlerts)

        return Result.success()
    }

    private fun notifyPair(pairAlert: PairAlert) {
        NotificationUtils.showPairAlert(pairAlert, context)
    }

    companion object {
        const val NAME = "CurrencyMonitorWorker"
    }
}
