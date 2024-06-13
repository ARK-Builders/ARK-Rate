package dev.arkbuilders.rate.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.arkbuilders.rate.BuildConfig
import dev.arkbuilders.rate.data.db.TimestampRepo
import dev.arkbuilders.rate.data.db.TimestampType
import dev.arkbuilders.rate.data.preferences.PrefsImpl
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

data class SettingsScreenState(
    val latestFiatRefresh: OffsetDateTime? = null,
    val latestCryptoRefresh: OffsetDateTime? = null,
    val latestPairAlertCheck: OffsetDateTime? = null,
    val showCrashReports: Boolean = BuildConfig.GOOGLE_PLAY_BUILD.not(),
    val crashReportsEnabled: Boolean = false
)

sealed class SettingsScreenEffect()

class SettingsViewModel(
    private val prefs: PrefsImpl,
    private val timestampRepo: TimestampRepo,
) : ViewModel(), ContainerHost<SettingsScreenState, SettingsScreenEffect> {
    override val container: Container<SettingsScreenState, SettingsScreenEffect> =
        container(SettingsScreenState())

    init {
        intent {
            val fiat = timestampRepo.getTimestamp(TimestampType.FetchFiat)
            val crypto = timestampRepo.getTimestamp(TimestampType.FetchCrypto)
            val pairAlertCheck =
                timestampRepo.getTimestamp(TimestampType.CheckPairAlerts)

            reduce {
                state.copy(
                    latestFiatRefresh = fiat,
                    latestCryptoRefresh = crypto,
                    latestPairAlertCheck = pairAlertCheck
                )
            }
        }
    }

}

@Singleton
class SettingsViewModelFactory @Inject constructor(
    private val prefs: PrefsImpl,
    private val timestampRepo: TimestampRepo,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(prefs, timestampRepo) as T
    }
}