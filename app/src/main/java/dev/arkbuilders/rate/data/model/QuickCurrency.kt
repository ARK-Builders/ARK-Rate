package dev.arkbuilders.rate.data.model

class QuickCurrency(
    val code: CurrencyCode,
    val usedCount: Int,
    val usedTime: Long
)

class QuickBaseCurrency(
    val code: CurrencyCode
)