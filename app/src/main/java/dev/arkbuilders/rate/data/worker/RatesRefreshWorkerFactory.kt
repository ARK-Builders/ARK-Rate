package dev.arkbuilders.rate.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase

class RatesRefreshWorkerFactory(
    private val quickRepo: QuickRepo,
    private val convertUseCase: ConvertWithRateUseCase,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            RatesRefreshWorker::class.java.name -> RatesRefreshWorker(
                params = workerParameters,
                context = appContext,
                quickRepo = quickRepo,
                convertUseCase = convertUseCase
            )
            else -> null
        }
    }
}