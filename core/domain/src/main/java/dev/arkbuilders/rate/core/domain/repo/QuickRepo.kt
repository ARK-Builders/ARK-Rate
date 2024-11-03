package dev.arkbuilders.rate.core.domain.repo

import dev.arkbuilders.rate.core.domain.model.QuickPair
import kotlinx.coroutines.flow.Flow

interface QuickRepo {
    suspend fun insert(quick: QuickPair)

    suspend fun getById(id: Long): QuickPair?

    suspend fun getAll(): List<QuickPair>

    suspend fun getAllGroups(): List<String?> = getAll().groupBy { it.group }.map { it.key }

    fun allFlow(): Flow<List<QuickPair>>

    suspend fun delete(id: Long): Boolean
}
