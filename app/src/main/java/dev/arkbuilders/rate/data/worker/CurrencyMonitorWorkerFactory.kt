package dev.arkbuilders.rate.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.domain.repo.TimestampRepo
import dev.arkbuilders.rate.domain.usecase.HandlePairAlertCheckUseCase

class CurrencyMonitorWorkerFactory(
    private val handlePairAlertCheckUseCase: HandlePairAlertCheckUseCase,
    private val timestampRepo: TimestampRepo,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            CurrencyMonitorWorker::class.java.name ->
                CurrencyMonitorWorker(
                    appContext,
                    workerParameters,
                    handlePairAlertCheckUseCase,
                    timestampRepo,
                )

            else -> null
        }
    }
}
