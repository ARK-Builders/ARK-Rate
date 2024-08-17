package dev.arkbuilders.rate.domain.model

import java.time.OffsetDateTime

data class QuickPair(
    val id: Long,
    val from: CurrencyCode,
    val amount: Double,
    val to: List<Amount>,
    val calculatedDate: OffsetDateTime,
    val pinnedDate: OffsetDateTime?,
    val group: String?
) {
    fun isPinned() = pinnedDate != null
}

data class PinnedQuickPair(
    val pair: QuickPair,
    val actualTo: List<Amount>,
    val refreshDate: OffsetDateTime
)
