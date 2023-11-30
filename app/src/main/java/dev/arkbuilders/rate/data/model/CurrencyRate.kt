package dev.arkbuilders.rate.data.model

data class CurrencyRate(
    val type: CurrencyType,
    val code: CurrencyCode,
    val rate: Double
)