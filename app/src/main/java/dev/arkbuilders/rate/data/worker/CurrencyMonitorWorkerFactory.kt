package dev.arkbuilders.rate.data.worker

import android.content.Context
import androidx.work.DelegatingWorkerFactory
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.db.PairAlertRepo

class CurrencyMonitorWorkerFactory(
    private val currencyRepo: GeneralCurrencyRepo,
    private val pairAlertRepo: PairAlertRepo
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            CurrencyMonitorWorker::class.java.name -> CurrencyMonitorWorker(
                appContext,
                workerParameters,
                currencyRepo,
                pairAlertRepo
            )

            else -> null
        }
    }
}