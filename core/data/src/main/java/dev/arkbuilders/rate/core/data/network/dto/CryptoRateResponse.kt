package dev.arkbuilders.rate.core.data.network.dto

import androidx.annotation.Keep

@Keep
data class CryptoRateResponse(
    val symbol: String,
    val current_price: Double,
)
