package dev.arkbuilders.rate.data.network.dto

data class CryptoRateResponse(
    val symbol: String,
    val current_price: Double,
)
