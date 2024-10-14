package dev.arkbuilders.ratewatch.domain.repo

import dev.arkbuilders.ratewatch.domain.model.CurrencyCode
import kotlinx.coroutines.flow.Flow

sealed class PreferenceKey<out T>(val defaultValue: T) {
    data object CollectCrashReports : PreferenceKey<Boolean>(true)

    data object CollectAnalytics : PreferenceKey<Boolean>(true)

    data object BaseCurrencyCode : PreferenceKey<CurrencyCode>("USD")
}

interface Prefs {
    suspend fun <T> get(key: PreferenceKey<T>): T

    suspend fun <T> set(
        key: PreferenceKey<T>,
        value: T,
    )

    fun <T> flow(key: PreferenceKey<T>): Flow<T>
}
