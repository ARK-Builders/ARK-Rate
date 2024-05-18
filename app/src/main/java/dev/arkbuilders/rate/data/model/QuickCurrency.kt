package dev.arkbuilders.rate.data.model

data class QuickPair(
    val id: Long,
    val from: CurrencyCode,
    val amount: Double,
    val to: List<CurrencyCode>,
    val group: String?
)
