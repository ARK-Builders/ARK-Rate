package dev.arkbuilders.rate.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

sealed class PreferenceKey<out T>(val defaultValue: T) {
    object QuickScreenTagCloud: PreferenceKey<Boolean>(true)
}

@Singleton
class Preferences @Inject constructor(val context: Context)  {
    private val SHARED_PREFERENCES_KEY = "user_preferences"

    private val Context.preferencesDatastore by preferencesDataStore(
        SHARED_PREFERENCES_KEY
    )

    private val dataStore = context.preferencesDatastore

    suspend fun <T> get(key: PreferenceKey<T>): T {
        val prefKey = resolveKey(key)
        return dataStore.data.first()[prefKey] ?: key.defaultValue
    }

    suspend fun <T> set(key: PreferenceKey<T>, value: T) {
        dataStore.edit { pref ->
            val prefKey = resolveKey(key)
            pref[prefKey] = value
        }
    }

    suspend fun <T> flow(key: PreferenceKey<T>) =
        dataStore.data.map { pref ->
            val prefKey = resolveKey(key)
            pref[prefKey] ?: key.defaultValue
        }

    private fun <T> resolveKey(key: PreferenceKey<T>): Preferences.Key<T> {
        val result = when (key) {
            PreferenceKey.QuickScreenTagCloud ->
                booleanPreferencesKey("quick_screen_tag_cloud")
        }

        return result as Preferences.Key<T>
    }

}