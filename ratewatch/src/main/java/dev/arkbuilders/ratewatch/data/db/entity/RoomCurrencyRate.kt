package dev.arkbuilders.ratewatch.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.arkbuilders.ratewatch.domain.model.CurrencyCode

@Entity
data class RoomCurrencyRate(
    @PrimaryKey
    val code: CurrencyCode,
    val currencyType: String,
    val rate: Double,
)
