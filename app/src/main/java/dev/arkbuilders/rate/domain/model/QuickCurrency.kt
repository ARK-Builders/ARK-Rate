package dev.arkbuilders.rate.domain.model

import dev.arkbuilders.rate.domain.model.CurrencyCode

data class QuickPair(
    val id: Long,
    val from: CurrencyCode,
    val amount: Double,
    val to: List<CurrencyCode>,
    val group: String?
)
