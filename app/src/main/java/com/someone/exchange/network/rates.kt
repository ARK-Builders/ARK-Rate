package com.someone.exchange.network

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

@JsonClass(generateAdapter = true)
data class rates(
    val base: String,
    val disclaimer: String,
    val license: String,
    val rates: Map<String, Double>,
    val timestamp: Int
)

fun getAllRates(): rates {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://raw.githubusercontent.com/someone120/ARK-Rate/dev-someone120/rates-cache/latest.json")
        .build()

    val response = client.newCall(request).execute().body!!.string()

    val moshi: Moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter(rates::class.java)
    val json = jsonAdapter.fromJson(response)

    return json!!
}

fun getAllRatesWithCache(filePath: String): Map<String, Double> {
    val file = File((File(filePath).parent ?: "") + "/rateCache.json")
    val moshi: Moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter(rates::class.java)
    if (!file.canWrite()) {
        val result = getAllRates()
        file.writeText(jsonAdapter.toJson(result))
        return result.rates
    }
    val origin = file.readText()
    val json = jsonAdapter.fromJson(origin)
    if ((json?.timestamp ?: 0) + 86400 < System.currentTimeMillis() / 1000) {
        val result = getAllRates()
        file.writeText(jsonAdapter.toJson(result))
        return result.rates
    }
    return json!!.rates
}