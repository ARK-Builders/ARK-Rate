package dev.arkbuilders.rate.data.network.dto

import androidx.annotation.Keep

@Keep
data class CryptoRateResponse(
    val symbol: String,
    val current_price: Double,
)
