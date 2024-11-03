package dev.arkbuilders.rate.core.domain.model

import java.math.BigDecimal
import java.time.OffsetDateTime

data class PairAlert(
    val id: Long,
    val targetCode: CurrencyCode,
    val baseCode: CurrencyCode,
    val targetPrice: BigDecimal,
    val startPrice: BigDecimal,
    val percent: Double?,
    val oneTimeNotRecurrent: Boolean,
    val enabled: Boolean,
    val lastDateTriggered: OffsetDateTime?,
    val group: String?,
) {
    fun above() = targetPrice > startPrice

    fun triggered() = lastDateTriggered != null

    fun byPriceStep() = targetPrice - startPrice
}
