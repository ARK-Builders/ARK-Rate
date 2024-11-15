package dev.arkbuilders.rate.feature.pairalert.domain.repo

import kotlinx.coroutines.flow.Flow

interface PairAlertRepo {
    suspend fun insert(pairAlert: dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert): Long

    suspend fun getById(id: Long): dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert?

    suspend fun getAll(): List<dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert>

    fun getAllFlow(): Flow<List<dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert>>

    suspend fun delete(id: Long): Boolean
}
