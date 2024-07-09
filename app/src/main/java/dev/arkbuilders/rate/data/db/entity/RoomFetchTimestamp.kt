package dev.arkbuilders.rate.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity
data class RoomFetchTimestamp(
    @PrimaryKey
    val currencyType: String,
    val timestamp: OffsetDateTime
)