package dev.arkbuilders.rate.feature.pairalert.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.feature.pairalert.domain.usecase.HandlePairAlertCheckUseCase

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
