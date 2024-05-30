package dev.arkbuilders.rate.domain.repo

import dev.arkbuilders.rate.domain.model.CurrencyCode
import kotlinx.coroutines.flow.Flow

sealed class PreferenceKey<out T>(val defaultValue: T) {
    data object QuickScreenShowAsKey : PreferenceKey<Int>(0)
    data object QuickScreenSortedByKey : PreferenceKey<Int>(0)
    data object FiatFiatRateRound : PreferenceKey<Int>(2)
    data object CryptoCryptoRateRound : PreferenceKey<Int>(2)
    data object FiatCryptoRateRound : PreferenceKey<Int>(2)
    data object CrashReport : PreferenceKey<Boolean>(true)
    data object BaseCurrencyCode: PreferenceKey<CurrencyCode>("USD")
}

interface Prefs {
    suspend fun <T> get(key: PreferenceKey<T>): T
    suspend fun <T> set(key: PreferenceKey<T>, value: T)
    fun <T> flow(key: PreferenceKey<T>): Flow<T>
}