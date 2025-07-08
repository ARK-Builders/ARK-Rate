package dev.arkbuilders.rate.core.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.core.domain.repo.Prefs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PrefsImpl(val context: Context) : Prefs {
    private val sharedPreferencesKey = "user_preferences"

    private val Context.preferencesDatastore by preferencesDataStore(
        sharedPreferencesKey,
    )

    private val dataStore = context.preferencesDatastore

    override suspend fun <T> get(key: PreferenceKey<T>): T {
        val prefKey = resolveKey(key)
        return dataStore.data.first()[prefKey] ?: key.defaultValue
    }

    override suspend fun <T> set(
        key: PreferenceKey<T>,
        value: T,
    ) {
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
        val result =
            when (key) {
                PreferenceKey.CollectAnalytics ->
                    booleanPreferencesKey("analytics")

                PreferenceKey.BaseCurrencyCode ->
                    stringPreferencesKey("baseCurrencyCode")

                PreferenceKey.CollectCrashReports ->
                    booleanPreferencesKey("crashReports")

                PreferenceKey.IsOnboardingCompleted ->
                    booleanPreferencesKey("isOnboardingCompleted")

                PreferenceKey.IsOnboardingQuickPairCompleted ->
                    booleanPreferencesKey("IsOnboardingQuickPairCompleted")

                PreferenceKey.CurrentVersionCode ->
                    intPreferencesKey("CurrentVersionCode")

                PreferenceKey.FirstInstallVersionCode ->
                    intPreferencesKey("FirstInstallVersionCode")

                PreferenceKey.IsFirstLaunch -> booleanPreferencesKey("IsFirstLaunch")
            }

        return result as Preferences.Key<T>
    }
}
