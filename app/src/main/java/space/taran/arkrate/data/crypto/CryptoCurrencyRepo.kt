package space.taran.arkrate.data.crypto

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import space.taran.arkrate.data.CurrencyRate
import space.taran.arkrate.data.CurrencyRepo
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoCurrencyRepo @Inject constructor(
    private val cryptoAPI: CryptoAPI
) : CurrencyRepo {
    private var cryptoWithRates: List<CurrencyRate>? = null
        set(value) {
            updatedTS = System.currentTimeMillis()
            field = value
        }
    private var updatedTS: Long? = null

    override suspend fun codeToRate(): List<CurrencyRate> =
        withContext(Dispatchers.IO) {
            Log.d("T", "$dayInMillis")
            if (cryptoWithRates == null ||
                updatedTS!! + dayInMillis < System.currentTimeMillis()
            ) {
                cryptoWithRates = cryptoAPI.getCryptoRates().findUSDTPairs()
            }

            cryptoWithRates!!
        }

    override suspend fun codeToCurrency(): Map<String, String> =
        codeToRate().map { (key, _) ->
            key to ""
        }.toMap()

    // api returns pairs like ETHBTC, ETHBNB, ETHTRX, ETHUSDT
    // we only take USDT pairs
    private fun List<CryptoRateResponse>.findUSDTPairs() =
        mapNotNull { (code, price) ->
            if (code.takeLast(4) == "USDT") {
                CurrencyRate(code.dropLast(4), price)
            } else
                null
        }

    private val dayInMillis = TimeUnit.DAYS.toMillis(1)
}