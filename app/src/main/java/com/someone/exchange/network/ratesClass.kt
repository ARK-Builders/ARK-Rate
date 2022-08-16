package com.someone.exchange.network


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ratesClass(
    @Json(name = "base")
    val base: String = "",
    @Json(name = "disclaimer")
    val disclaimer: String = "",
    @Json(name = "license")
    val license: String = "",
    @Json(name = "rates")
    val rates: Rates = Rates(),
    @Json(name = "timestamp")
    val timestamp: Int = 0
)

