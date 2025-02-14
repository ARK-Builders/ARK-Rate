package dev.arkbuilders.rate.core.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import java.math.BigDecimal

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
data class RoomAsset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: CurrencyCode,
    val amount: BigDecimal,
    val groupId: Long,
)
