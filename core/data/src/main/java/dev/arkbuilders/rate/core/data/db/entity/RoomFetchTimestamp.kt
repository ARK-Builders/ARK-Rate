package dev.arkbuilders.rate.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity
data class RoomFetchTimestamp(
    @PrimaryKey
    val type: String,
    val timestamp: OffsetDateTime,
)
