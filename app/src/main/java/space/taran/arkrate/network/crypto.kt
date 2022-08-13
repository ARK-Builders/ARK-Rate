package space.taran.arkrate.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

@JsonClass(generateAdapter = true)
data class BinancePrizeItem(
    @Json(name = "price")
    val price: String,
    @Json(name = "symbol")
    val symbol: String
)

@JsonClass(generateAdapter = true)
data class BinancePrizeTimestamp(
    val timestamp: Int,
    val binancePrize: Map<String, Double>
)

class Crypto {
    private fun getCryptoPrice(): Map<String, Double> {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.binance.com/api/v3/ticker/price")
            .build()

        val response = client.newCall(request).execute().body!!.string()
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<List<BinancePrizeItem>>(
            Types.newParameterizedType(
                List::class.java,
                BinancePrizeItem::class.java
            )
        )

        val json = jsonAdapter.fromJson(response)
        val result = mutableMapOf<String, Double>()
        json!!.forEach {
            result[it.symbol] = it.price.toDouble()
        }
        return result
    }

    fun getCryptoPriceWithCache(filePath: String): Map<String, Double> {
        val file = File((File(filePath).parent ?: "") + "/CryptoCache.json")
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(BinancePrizeTimestamp::class.java)
        if (!file.canWrite()) {
            val result = BinancePrizeTimestamp(
                timestamp = (System.currentTimeMillis() / 1000).toInt(),
                getCryptoPrice()
            )
            file.writeText(jsonAdapter.toJson(result))
            return result.binancePrize
        }
        val origin = file.readText()
        val json = jsonAdapter.fromJson(origin)
        if ((json?.timestamp ?: 0) + 86400 < System.currentTimeMillis() / 1000) {
            val result = BinancePrizeTimestamp(
                timestamp = (System.currentTimeMillis() / 1000).toInt(),
                getCryptoPrice()
            )
            file.writeText(jsonAdapter.toJson(result))
            return result.binancePrize
        }
        return json!!.binancePrize
    }
}