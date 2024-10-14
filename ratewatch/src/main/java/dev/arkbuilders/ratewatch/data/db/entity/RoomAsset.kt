package dev.arkbuilders.ratewatch.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.arkbuilders.ratewatch.domain.model.CurrencyCode

@Entity
data class RoomAsset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: CurrencyCode,
    val amount: Double,
    val group: String?,
)
