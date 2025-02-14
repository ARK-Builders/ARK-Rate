package dev.arkbuilders.rate.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import java.time.OffsetDateTime

@Entity
class RoomGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val orderIndex: Int,
    val creationTime: OffsetDateTime,
    val featureType: GroupFeatureType,
)
