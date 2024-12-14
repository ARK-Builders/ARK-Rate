package dev.arkbuilders.rate.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import java.time.OffsetDateTime

@Entity
data class RoomCodeUseStat(
    @PrimaryKey
    val code: CurrencyCode,
    val count: Long,
    val lastUsedDate: OffsetDateTime,
)
