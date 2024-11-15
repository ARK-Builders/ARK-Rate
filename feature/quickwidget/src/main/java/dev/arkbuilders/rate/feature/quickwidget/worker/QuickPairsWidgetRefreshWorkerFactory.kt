package dev.arkbuilders.rate.feature.quickwidget.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class QuickPairsWidgetRefreshWorkerFactory : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            QuickPairsWidgetRefreshWorker::class.java.name ->
                QuickPairsWidgetRefreshWorker(
                    params = workerParameters,
                    context = appContext,
                )

            else -> null
        }
    }
}
