package dev.arkbuilders.rate.presentation

import android.app.Application
import android.os.Build
import android.webkit.WebView
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dev.arkbuilders.rate.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dev.arkbuilders.rate.data.worker.CurrencyMonitorWorker
import dev.arkbuilders.rate.data.worker.RatesRefreshWorker
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.domain.repo.PreferenceKey
import timber.log.Timber
import java.util.concurrent.TimeUnit

class App : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        DIManager.init(this)

        initCrashlytics()
        initWorker()
        initRateRefreshWorker()
    }

    private fun initCrashlytics() = CoroutineScope(Dispatchers.IO).launch {
        // Google Play will collect crashes in any case, so we will also send them to Firebase
        val collect = if (BuildConfig.GOOGLE_PLAY_BUILD)
            true
        else
            DIManager.component.prefs().get(PreferenceKey.CollectCrashReports)

        Firebase.crashlytics.setCrashlyticsCollectionEnabled(collect)
    }


    private fun initWorker() {
        val workManager = WorkManager.getInstance(this)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest =
            PeriodicWorkRequest.Builder(
                CurrencyMonitorWorker::class.java,
                8L,
                TimeUnit.HOURS
            ).setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            CurrencyMonitorWorker.name,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun initRateRefreshWorker() {
        val workManager = WorkManager.getInstance(this)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest =
            PeriodicWorkRequest.Builder(
                RatesRefreshWorker::class.java,
                15,
                TimeUnit.MINUTES
            ).setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            RatesRefreshWorker.name,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setMinimumLoggingLevel(android.util.Log.INFO)
        .setWorkerFactory(DIManager.component.appWorkerFactory())
        .build()
}
