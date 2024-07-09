package dev.arkbuilders.rate.domain.model

import dev.arkbuilders.rate.domain.model.CurrencyCode
import java.time.OffsetDateTime

data class QuickPair(
    val id: Long,
    val from: CurrencyCode,
    val amount: Double,
    val to: List<Amount>,
    val calculatedDate: OffsetDateTime,
    val group: String?,
)
