package dev.arkbuilders.rate.data.model

import dev.arkbuilders.rate.data.model.CurrencyCode

class QuickCurrency(
    val code: CurrencyCode,
    val usedCount: Int,
    val usedTime: Long
)