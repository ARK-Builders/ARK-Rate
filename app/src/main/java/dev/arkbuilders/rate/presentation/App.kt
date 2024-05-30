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
import dev.arkbuilders.rate.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.acra.config.dialog
import org.acra.config.httpSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.acra.sender.HttpSender
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.worker.CurrencyMonitorWorker
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.domain.repo.PreferenceKey
import timber.log.Timber
import java.util.concurrent.TimeUnit

class App : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        fixWebViewDataDirectorySuffix()
        Timber.plant(Timber.DebugTree())
        DIManager.init(this)
        
        if (!BuildConfig.GOOGLE_PLAY_BUILD)
            initAcra()

        initWorker()
    }

    private fun initAcra() = CoroutineScope(Dispatchers.IO).launch {
        if (!DIManager.component.prefs().get(PreferenceKey.CrashReport))
            return@launch

        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            dialog {
                text = getString(R.string.crash_dialog_description)
                title = getString(R.string.crash_dialog_title)
                commentPrompt = getString(R.string.crash_dialog_comment)
            }
            httpSender {
                uri = BuildConfig.ACRA_URI
                basicAuthLogin = BuildConfig.ACRA_LOGIN
                basicAuthPassword = BuildConfig.ACRA_PASS
                httpMethod = HttpSender.Method.POST
            }
        }
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

    private fun fixWebViewDataDirectorySuffix() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val process = getProcessName()
            if (packageName != process) WebView.setDataDirectorySuffix(process)
        }
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setMinimumLoggingLevel(android.util.Log.INFO)
        .setWorkerFactory(DIManager.component.appWorkerFactory())
        .build()
}
