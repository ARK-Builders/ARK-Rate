package dev.arkbuilders.ratewatch.presentation

import android.app.Application
import dev.arkbuilders.ratewatch.di.DIManager

class RateWatchApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DIManager.init(this)
    }
}
