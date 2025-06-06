package dev.arkbuilders.rate.feature.quick.domain.repo

import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import kotlinx.coroutines.flow.Flow

interface QuickRepo {
    suspend fun insert(quick: QuickPair): Long

    suspend fun getById(id: Long): QuickPair?

    suspend fun getAll(): List<QuickPair>

    suspend fun getAllGroups(): List<Group> = getAll().groupBy { it.group }.map { it.key }

    fun allFlow(): Flow<List<QuickPair>>

    suspend fun delete(id: Long): Boolean
}
