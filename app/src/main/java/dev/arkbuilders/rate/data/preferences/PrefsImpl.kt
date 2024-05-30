package dev.arkbuilders.rate.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.arkbuilders.rate.domain.repo.PreferenceKey
import dev.arkbuilders.rate.domain.repo.Prefs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsImpl @Inject constructor(val context: Context): Prefs {
    private val SHARED_PREFERENCES_KEY = "user_preferences"

    private val Context.preferencesDatastore by preferencesDataStore(
        SHARED_PREFERENCES_KEY
    )

    private val dataStore = context.preferencesDatastore

    override suspend fun <T> get(key: PreferenceKey<T>): T {
        val prefKey = resolveKey(key)
        return dataStore.data.first()[prefKey] ?: key.defaultValue
    }

    override suspend fun <T> set(key: PreferenceKey<T>, value: T) {
        dataStore.edit { pref ->
            val prefKey = resolveKey(key)
            pref[prefKey] = value
        }
    }

    override fun <T> flow(key: PreferenceKey<T>) =
        dataStore.data.map { pref ->
            val prefKey = resolveKey(key)
            pref[prefKey] ?: key.defaultValue
        }

    private fun <T> resolveKey(key: PreferenceKey<T>): Preferences.Key<T> {
        val result = when (key) {
            PreferenceKey.QuickScreenSortedByKey ->
                intPreferencesKey("quick_screen_sorted_by")

            PreferenceKey.QuickScreenShowAsKey ->
                intPreferencesKey("quick_screen_show_as")

            PreferenceKey.FiatFiatRateRound ->
                intPreferencesKey("round_fiat_fiat")

            PreferenceKey.CryptoCryptoRateRound ->
                intPreferencesKey("round_crypto_crypto")

            PreferenceKey.FiatCryptoRateRound ->
                intPreferencesKey("round_crypto_fiat")

            PreferenceKey.CrashReport -> booleanPreferencesKey("crash_report")

            PreferenceKey.BaseCurrencyCode -> stringPreferencesKey("baseCurrencyCode")

        }

        return result as Preferences.Key<T>
    }

}