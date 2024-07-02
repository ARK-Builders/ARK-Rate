package dev.arkbuilders.rate.data.repo

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import dev.arkbuilders.rate.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.domain.repo.PreferenceKey
import dev.arkbuilders.rate.domain.repo.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManagerImpl @Inject constructor(
    private val prefs: Prefs
) : AnalyticsManager {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun trackScreen(name: String) = needToCollect {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, name)
        }
    }

    private fun needToCollect(action: () -> Unit) {
        scope.launch {
            val collect = prefs.get(PreferenceKey.CollectAnalytics)
            if (collect)
                action()
        }
    }
}