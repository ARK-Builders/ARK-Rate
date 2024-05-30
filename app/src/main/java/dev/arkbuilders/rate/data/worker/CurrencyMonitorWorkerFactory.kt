package dev.arkbuilders.rate.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.data.currency.CurrencyRepoImpl
import dev.arkbuilders.rate.data.db.PairAlertRepoImpl

class CurrencyMonitorWorkerFactory(
    private val currencyRepo: CurrencyRepoImpl,
    private val pairAlertRepo: PairAlertRepoImpl
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