package dev.arkbuilders.rate.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomFetchTimestamp(
    @PrimaryKey
    val currencyType: String,
    val timestamp: String
)