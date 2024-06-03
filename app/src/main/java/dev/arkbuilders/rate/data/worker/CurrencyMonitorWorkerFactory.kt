package dev.arkbuilders.rate.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.domain.usecase.HandlePairAlertCheckUseCase

class CurrencyMonitorWorkerFactory(
    private val handlePairAlertCheckUseCase: HandlePairAlertCheckUseCase
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
                handlePairAlertCheckUseCase
            )

            else -> null
        }
    }
}