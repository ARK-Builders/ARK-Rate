package dev.arkbuilders.rate.domain.repo

import dev.arkbuilders.rate.domain.model.PairAlert
import kotlinx.coroutines.flow.Flow

interface PairAlertRepo {
    suspend fun insert(pairAlert: PairAlert): Long
    suspend fun getAll(): List<PairAlert>
    fun getAllFlow(): Flow<List<PairAlert>>
    suspend fun delete(id: Long)
}