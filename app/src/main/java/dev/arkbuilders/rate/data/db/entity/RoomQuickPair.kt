package dev.arkbuilders.rate.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.domain.model.CurrencyCode
import java.time.OffsetDateTime

@Entity
data class RoomQuickPair(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val from: CurrencyCode,
    val amount: Double,
    val to: List<Amount>,
    val calculatedDate: OffsetDateTime,
    val pinnedDate: OffsetDateTime?,
    val group: String?
)