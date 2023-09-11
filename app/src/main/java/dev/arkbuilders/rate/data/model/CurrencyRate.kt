package dev.arkbuilders.rate.data.model

import dev.arkbuilders.rate.data.model.CurrencyCode

data class CurrencyRate(
    val type: CurrencyType,
    val code: CurrencyCode,
    val rate: Double
)