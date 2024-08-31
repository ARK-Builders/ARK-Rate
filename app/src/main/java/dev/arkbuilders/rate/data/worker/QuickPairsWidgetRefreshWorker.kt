package dev.arkbuilders.rate.data.worker

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.presentation.quick.glancewidget.QuickPairsWidgetReceiver
import timber.log.Timber

class QuickPairsWidgetRefreshWorker(
    params: WorkerParameters,
    private val context: Context,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Timber.d("Refresh rates work executed")
        val intent = Intent(applicationContext, QuickPairsWidgetReceiver::class.java).apply {
            action = QuickPairsWidgetReceiver.PINNED_PAIRS_REFRESH
        }
        applicationContext.sendBroadcast(intent)
        return Result.success()
    }

    companion object {
        const val NAME = "RatesRefreshWorker"
    }
}