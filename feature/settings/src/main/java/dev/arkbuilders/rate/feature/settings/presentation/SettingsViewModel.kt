package dev.arkbuilders.rate.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dev.arkbuilders.rate.core.domain.BuildConfigFields
import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider
import dev.arkbuilders.rate.core.domain.model.TimestampType
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.feature.settings.di.SettingsScope
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
    val showCrashReports: Boolean = false,
    val crashReportsEnabled: Boolean = false,
    val analyticsEnabled: Boolean = false,
)

sealed class SettingsScreenEffect()

class SettingsViewModel(
    private val prefs: Prefs,
    private val timestampRepo: TimestampRepo,
    private val analyticsManager: AnalyticsManager,
    private val buildConfigFields: BuildConfigFields,
) : ViewModel(), ContainerHost<SettingsScreenState, SettingsScreenEffect> {
    override val container: Container<SettingsScreenState, SettingsScreenEffect> =
        container(SettingsScreenState(showCrashReports = buildConfigFields.isGooglePlayBuild.not()))

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
                    analyticsEnabled = analytics,
                )
            }
        }
    }

    fun onCrashReportToggle(enabled: Boolean) =
        intent {
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)
            prefs.set(PreferenceKey.CollectCrashReports, enabled)
            reduce {
                state.copy(crashReportsEnabled = enabled)
            }
        }

    fun onAnalyticsToggle(enabled: Boolean) =
        intent {
            Firebase.analytics.setAnalyticsCollectionEnabled(enabled)
            prefs.set(PreferenceKey.CollectAnalytics, enabled)
            reduce {
                state.copy(analyticsEnabled = enabled)
            }
        }
}

@SettingsScope
class SettingsViewModelFactory @Inject constructor(
    private val prefs: Prefs,
    private val timestampRepo: TimestampRepo,
    private val analyticsManager: AnalyticsManager,
    private val buildConfigFieldsProvider: BuildConfigFieldsProvider,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            prefs,
            timestampRepo,
            analyticsManager,
            buildConfigFieldsProvider.provide()
        ) as T
    }
}
