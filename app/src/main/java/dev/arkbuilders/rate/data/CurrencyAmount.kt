package dev.arkbuilders.rate.data

data class CurrencyAmount(
    val id: Long = 0,
    val code: CurrencyCode,
    var amount: Double
)