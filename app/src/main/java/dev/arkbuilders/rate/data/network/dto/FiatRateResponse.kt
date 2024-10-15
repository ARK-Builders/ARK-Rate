package dev.arkbuilders.rate.data.network.dto

import androidx.annotation.Keep

@Keep
data class FiatRateResponse(
    val timestamp: Long,
    val rates: Map<String, Double>,
)
