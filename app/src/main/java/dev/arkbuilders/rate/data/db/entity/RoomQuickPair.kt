package dev.arkbuilders.rate.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.arkbuilders.rate.domain.model.CurrencyCode

@Entity
data class RoomQuickPair(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val from: CurrencyCode,
    val amount: Double,
    val to: String,
    val group: String?
)