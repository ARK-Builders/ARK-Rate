package dev.arkbuilders.rate.core.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.core.db.entity.RoomGroup
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType

@Dao
interface GroupDao {
    @Upsert
    suspend fun insert(group: RoomGroup): Long

    @Query("SELECT * FROM RoomGroup WHERE featureType = :featureType")
    fun getAllByFeatureType(featureType: GroupFeatureType): List<RoomGroup>

    @Query("SELECT * FROM RoomGroup WHERE isDefault = 1 AND featureType = :featureType")
    fun getDefaultByFeatureType(featureType: GroupFeatureType): RoomGroup?

    @Query("DELETE FROM RoomGroup WHERE id = :id")
    suspend fun delete(id: Long): Int
}
