package dev.arkbuilders.rate.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.arkbuilders.rate.domain.model.CurrencyCode

@Entity
data class RoomPairAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val targetCode: CurrencyCode,
    val baseCode: CurrencyCode,
    val targetPrice: Double,
    val startPrice: Double,
    val alertPercent: Double?,
    val oneTimeNotRecurrent: Boolean,
    val enabled: Boolean,
    val lastDateTriggered: String?,
    val group: String?
)