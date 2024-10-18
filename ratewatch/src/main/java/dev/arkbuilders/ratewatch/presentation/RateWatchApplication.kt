package dev.arkbuilders.ratewatch.presentation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RateWatchApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
