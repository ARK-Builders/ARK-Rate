package dev.arkbuilders.rate.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.arkbuilders.rate.domain.model.CurrencyCode

@Entity
data class RoomCurrencyRate(
    @PrimaryKey
    val code: CurrencyCode,
    val currencyType: String,
    val rate: Double,
)
