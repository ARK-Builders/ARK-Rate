package space.taran.arkrate.presentation

import android.app.Application
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
import space.taran.arkrate.di.DIManager
import space.taran.arkrate.utils.Config

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initAcra()
        DIManager.init(this)
    }

    private fun initAcra() = CoroutineScope(Dispatchers.IO).launch {
        val enabled = Config.newInstance(context = baseContext).crashReport
        if (!enabled) return@launch

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
}
