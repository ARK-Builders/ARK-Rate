package dev.arkbuilders.rate.domain.repo

import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.stats.CodeUseStat

interface CodeUseStatRepo {
    suspend fun codesUsed(vararg codes: CurrencyCode)
    suspend fun getAll(): Map<CurrencyCode, CodeUseStat>
}