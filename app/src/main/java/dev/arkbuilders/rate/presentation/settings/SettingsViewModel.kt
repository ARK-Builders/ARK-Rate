package dev.arkbuilders.rate.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dev.arkbuilders.rate.BuildConfig
import dev.arkbuilders.rate.data.preferences.PrefsImpl
import dev.arkbuilders.rate.domain.model.TimestampType
import dev.arkbuilders.rate.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.domain.repo.PreferenceKey
import dev.arkbuilders.rate.domain.repo.TimestampRepo
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

data class SettingsScreenState(
    val latestRefresh: OffsetDateTime? = null,
    val latestPairAlertCheck: OffsetDateTime? = null,
    val showCrashReports: Boolean = BuildConfig.GOOGLE_PLAY_BUILD.not(),
    val crashReportsEnabled: Boolean = false,
    val analyticsEnabled: Boolean = false
)

sealed class SettingsScreenEffect()

class SettingsViewModel(
    private val prefs: PrefsImpl,
    private val timestampRepo: TimestampRepo,
    private val analyticsManager: AnalyticsManager
) : ViewModel(), ContainerHost<SettingsScreenState, SettingsScreenEffect> {
    override val container: Container<SettingsScreenState, SettingsScreenEffect> =
        container(SettingsScreenState())

    init {
        analyticsManager.trackScreen("SettingsScreen")

        intent {
            timestampRepo
                .timestampFlow(TimestampType.FetchRates)
                .drop(1)
                .onEach {
                    reduce { state.copy(latestRefresh = it) }
                }.launchIn(viewModelScope)

            timestampRepo
                .timestampFlow(TimestampType.CheckPairAlerts)
                .drop(1)
                .onEach {
                    reduce { state.copy(latestPairAlertCheck = it) }
                }.launchIn(viewModelScope)

            val refresh = timestampRepo.getTimestamp(TimestampType.FetchRates)
            val pairAlertCheck =
                timestampRepo.getTimestamp(TimestampType.CheckPairAlerts)
            val crashReports = prefs.get(PreferenceKey.CollectCrashReports)
            val analytics = prefs.get(PreferenceKey.CollectAnalytics)

            reduce {
                state.copy(
                    latestRefresh = refresh,
                    latestPairAlertCheck = pairAlertCheck,
                    crashReportsEnabled = crashReports,
                    analyticsEnabled = analytics
                )
            }
        }
    }

    fun onCrashReportToggle(enabled: Boolean) = intent {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)
        prefs.set(PreferenceKey.CollectCrashReports, enabled)
        reduce {
            state.copy(crashReportsEnabled = enabled)
        }
    }

    fun onAnalyticsToggle(enabled: Boolean) = intent {
        Firebase.analytics.setAnalyticsCollectionEnabled(enabled)
        prefs.set(PreferenceKey.CollectAnalytics, enabled)
        reduce {
            state.copy(analyticsEnabled = enabled)
        }
    }
}

@Singleton
class SettingsViewModelFactory @Inject constructor(
    private val prefs: PrefsImpl,
    private val timestampRepo: TimestampRepo,
    private val analyticsManager: AnalyticsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(prefs, timestampRepo, analyticsManager) as T
    }
}