package space.taran.arkrate.presentation

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.acra.config.dialog
import org.acra.config.httpSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.acra.sender.HttpSender
import space.taran.arkrate.BuildConfig
import space.taran.arkrate.R
import space.taran.arkrate.data.worker.CurrencyMonitorWorker
import space.taran.arkrate.di.DIManager
import java.util.concurrent.TimeUnit

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initAcra()
        DIManager.init(this)

        initWorker()
    }

    private fun initAcra() = CoroutineScope(Dispatchers.IO).launch {
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
}
