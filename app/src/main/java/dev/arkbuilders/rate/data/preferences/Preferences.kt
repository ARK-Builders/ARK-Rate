package dev.arkbuilders.rate.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class QuickScreenShowAs {
    TAG_CLOUD, LIST, GRID
}

enum class QuickScreenSortedBy {
    USED_COUNT, USED_TIME
}

sealed class PreferenceKey<out T>(val defaultValue: T) {
    object QuickScreenShowAsKey : PreferenceKey<Int>(0)
    object QuickScreenSortedByKey : PreferenceKey<Int>(0)
    object FiatFiatRateRound : PreferenceKey<Int>(2)
    object CryptoCryptoRateRound : PreferenceKey<Int>(2)
    object FiatCryptoRateRound : PreferenceKey<Int>(2)
    object CrashReport : PreferenceKey<Boolean>(true)
}

@Singleton
class Preferences @Inject constructor(val context: Context) {
    private val SHARED_PREFERENCES_KEY = "user_preferences"

    private val Context.preferencesDatastore by preferencesDataStore(SHARED_PREFERENCES_KEY)

    private val dataStore = context.preferencesDatastore

    suspend fun <T> get(key: PreferenceKey<T>): T {
        val prefKey = resolveKey(key)
        return dataStore.data.first()[prefKey]
            ?: key.defaultValue
    }

    suspend fun <T> set(key: PreferenceKey<T>, value: T) {
        dataStore.edit { pref ->
            val prefKey = resolveKey(key)
            pref[prefKey] = value
        }
    }

    fun <T> flow(key: PreferenceKey<T>) = dataStore.data.map { pref ->
        val prefKey = resolveKey(key)
        pref[prefKey]
            ?: key.defaultValue
    }

    private fun <T> resolveKey(key: PreferenceKey<T>): Preferences.Key<T> {
        val result = when (key) {
            PreferenceKey.QuickScreenSortedByKey -> intPreferencesKey("quick_screen_sorted_by")

            PreferenceKey.QuickScreenShowAsKey -> intPreferencesKey("quick_screen_show_as")

            PreferenceKey.FiatFiatRateRound -> intPreferencesKey("round_fiat_fiat")

            PreferenceKey.CryptoCryptoRateRound -> intPreferencesKey("round_crypto_crypto")

            PreferenceKey.FiatCryptoRateRound -> intPreferencesKey("round_crypto_fiat")

            PreferenceKey.CrashReport -> booleanPreferencesKey("crash_report")

        }

        return result as Preferences.Key<T>
    }

}