package dev.arkbuilders.rate.data.model

class QuickCurrency(
    val code: CurrencyCode,
    val usedCount: Int,
    val usedTime: Long
)

class QuickConvertToCurrency(
    val code: CurrencyCode
)