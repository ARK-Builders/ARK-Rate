package dev.arkbuilders.rate.core.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.core.db.entity.RoomGroup
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Upsert
    suspend fun insert(group: RoomGroup): Long

    @Upsert
    suspend fun insert(groups: List<RoomGroup>)

    @Query("SELECT * FROM RoomGroup WHERE featureType = :featureType")
    fun allFlow(featureType: GroupFeatureType): Flow<List<RoomGroup>>

    @Query("SELECT * FROM RoomGroup WHERE featureType = :featureType")
    suspend fun getAllByFeatureType(featureType: GroupFeatureType): List<RoomGroup>

    @Query("SELECT * FROM RoomGroup WHERE id = :id")
    suspend fun getById(id: Long): RoomGroup

    @Query("DELETE FROM RoomGroup WHERE id = :id")
    suspend fun delete(id: Long): Int
}
