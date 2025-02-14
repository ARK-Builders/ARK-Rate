package dev.arkbuilders.rate.core.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
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
    val groupId: Long,
)
