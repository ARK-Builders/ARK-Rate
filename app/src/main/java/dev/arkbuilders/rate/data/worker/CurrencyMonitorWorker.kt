package dev.arkbuilders.rate.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.data.currency.CurrencyRepoImpl
import dev.arkbuilders.rate.data.db.PairAlertRepoImpl
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.model.PairAlert
import dev.arkbuilders.rate.domain.usecase.HandlePairAlertCheckUseCase
import dev.arkbuilders.rate.presentation.utils.NotificationUtils
import java.time.OffsetDateTime

class CurrencyMonitorWorker(
    private val context: Context,
    params: WorkerParameters,
    private val handlePairAlertCheckUseCase: HandlePairAlertCheckUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val pairsToNotify = handlePairAlertCheckUseCase()
        pairsToNotify.forEach { (pairAlert, currentRate) ->
            notifyPair(pairAlert, currentRate)
        }

        return Result.success()
    }

    private fun notifyPair(pairAlert: PairAlert, curRatio: Double) {
        NotificationUtils.showPairAlert(pairAlert, curRatio, context)
    }

    companion object {
        const val name = "CurrencyMonitorWorker"
    }
}