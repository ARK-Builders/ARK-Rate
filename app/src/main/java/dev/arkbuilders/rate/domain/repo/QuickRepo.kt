package dev.arkbuilders.rate.domain.repo

import dev.arkbuilders.rate.domain.model.PairAlert
import dev.arkbuilders.rate.domain.model.QuickPair
import kotlinx.coroutines.flow.Flow

interface QuickRepo {
    suspend fun insert(quick: QuickPair)

    suspend fun getAll(): List<QuickPair>

    fun allFlow(): Flow<List<QuickPair>>

    suspend fun delete(id: Long)
}