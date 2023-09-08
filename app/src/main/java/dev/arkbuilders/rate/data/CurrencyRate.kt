package dev.arkbuilders.rate.data

data class CurrencyRate(
    val type: CurrencyType,
    val code: CurrencyCode,
    val rate: Double
)