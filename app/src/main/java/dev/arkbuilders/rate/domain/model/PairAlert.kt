package dev.arkbuilders.rate.domain.model

import dev.arkbuilders.rate.domain.model.CurrencyCode
import java.time.OffsetDateTime

data class PairAlert(
    val id: Long,
    val targetCode: CurrencyCode,
    val baseCode: CurrencyCode,
    val targetPrice: Double,
    val startPrice: Double,
    val alertPercent: Double?,
    val oneTimeNotRecurrent: Boolean,
    val enabled: Boolean,
    val priceNotPercent: Boolean,
    val triggered: Boolean,
    val lastDateTriggered: OffsetDateTime?,
    val group: String?
) {
    fun above() = targetPrice > startPrice
}