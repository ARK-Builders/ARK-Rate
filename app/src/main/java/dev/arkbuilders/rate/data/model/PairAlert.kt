package dev.arkbuilders.rate.data.model

data class PairAlert(
    val id: Long,
    val targetCode: CurrencyCode,
    val baseCode: CurrencyCode,
    val targetPrice: Double,
    val startPrice: Double,
    val alertPercent: Double?,
    val oneTimeNotRecurrent: Boolean,
    val priceNotPercent: Boolean,
    val triggered: Boolean,
    val group: String?
) {
    fun above() = targetPrice > startPrice
}