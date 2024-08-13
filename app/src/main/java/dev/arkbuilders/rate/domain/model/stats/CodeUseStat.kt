package dev.arkbuilders.rate.domain.model.stats

import dev.arkbuilders.rate.domain.model.CurrencyCode
import java.time.OffsetDateTime

data class CodeUseStat(
    val code: CurrencyCode,
    val count: Long,
    val lastUsedDate: OffsetDateTime
)