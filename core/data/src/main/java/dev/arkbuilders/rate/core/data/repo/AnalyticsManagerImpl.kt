package dev.arkbuilders.rate.core.data.repo

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.Prefs

class AnalyticsManagerImpl(
    private val prefs: Prefs,
) : AnalyticsManager {
    override fun trackScreen(name: String) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, name)
        }
    }

    override fun logEvent(event: String) {
        Firebase.analytics.logEvent(event) {}
    }
}
