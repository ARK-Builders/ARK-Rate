package dev.arkbuilders.rate.domain.model

import java.time.OffsetDateTime

data class PairAlert(
    val id: Long,
    val targetCode: CurrencyCode,
    val baseCode: CurrencyCode,
    val targetPrice: Double,
    val startPrice: Double,
    val percent: Double?,
    val oneTimeNotRecurrent: Boolean,
    val enabled: Boolean,
    val lastDateTriggered: OffsetDateTime?,
    val group: String?
) {
    fun above() = targetPrice > startPrice
    fun triggered() = lastDateTriggered != null
    fun byPriceStep() = targetPrice - startPrice
}