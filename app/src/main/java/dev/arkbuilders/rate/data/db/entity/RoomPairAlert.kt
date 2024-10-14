package dev.arkbuilders.rate.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.arkbuilders.rate.domain.model.CurrencyCode
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
data class RoomPairAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val targetCode: CurrencyCode,
    val baseCode: CurrencyCode,
    val targetPrice: BigDecimal,
    val startPrice: BigDecimal,
    val alertPercent: Double?,
    val oneTimeNotRecurrent: Boolean,
    val enabled: Boolean,
    val lastDateTriggered: OffsetDateTime?,
    val group: String?,
)
