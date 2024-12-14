package dev.arkbuilders.rate.core.domain.repo

import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.stats.CodeUseStat
import kotlinx.coroutines.flow.Flow

interface CodeUseStatRepo {
    suspend fun codesUsed(vararg codes: CurrencyCode)

    suspend fun getAll(): List<CodeUseStat>

    fun getAllFlow(): Flow<List<CodeUseStat>>
}
