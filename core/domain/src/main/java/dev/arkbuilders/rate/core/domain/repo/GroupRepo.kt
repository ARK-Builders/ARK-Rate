package dev.arkbuilders.rate.core.domain.repo

import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType

interface GroupRepo {
    suspend fun new(
        name: String,
        featureType: GroupFeatureType,
    ): Group

    suspend fun update(
        updated: Group,
        featureType: GroupFeatureType,
    )

    suspend fun delete(id: Long)

    suspend fun getAllSorted(featureType: GroupFeatureType): List<Group>

    suspend fun getDefaultByFeatureType(featureType: GroupFeatureType): Group?
}
