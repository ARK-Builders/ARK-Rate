package dev.arkbuilders.rate.feature.quick.domain.model

import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.Group
import java.math.BigDecimal
import java.time.OffsetDateTime

data class QuickCalculation(
    val id: Long,
    val from: CurrencyCode,
    val amount: BigDecimal,
    val to: List<Amount>,
    val calculatedDate: OffsetDateTime,
    val pinnedDate: OffsetDateTime?,
    val group: Group,
) {
    fun isPinned() = pinnedDate != null
}

data class PinnedQuickCalculation(
    val pair: QuickCalculation,
    val actualTo: List<Amount>,
    val refreshDate: OffsetDateTime,
)
