package dev.arkbuilders.rate.core.domain.repo

import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import kotlinx.coroutines.flow.Flow

interface GroupRepo {
    suspend fun new(
        name: String,
        featureType: GroupFeatureType,
    ): Group

    suspend fun update(
        updated: Group,
        featureType: GroupFeatureType,
    ): Long

    suspend fun update(
        updated: List<Group>,
        featureType: GroupFeatureType,
    )

    fun allFlow(featureType: GroupFeatureType): Flow<List<Group>>

    suspend fun getById(id: Long): Group

    suspend fun getDefault(groupFeatureType: GroupFeatureType): Group?

    suspend fun delete(id: Long)

    suspend fun getAllSorted(featureType: GroupFeatureType): List<Group>
}
