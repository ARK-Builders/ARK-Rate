package space.taran.arkrate.data.crypto

import space.taran.arkrate.data.CurrencyName
import space.taran.arkrate.data.CurrencyRate
import space.taran.arkrate.data.CurrencyRepo
import space.taran.arkrate.data.CurrencyType
import space.taran.arkrate.data.network.NetworkStatus
import space.taran.arkrate.data.db.CurrencyRateLocalDataSource
import space.taran.arkrate.data.db.FetchTimestampDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoCurrencyRepo @Inject constructor(
    private val cryptoAPI: CryptoAPI,
    private val local: CurrencyRateLocalDataSource,
    private val networkStatus: NetworkStatus,
    private val fetchTimestampDataSource: FetchTimestampDataSource
) : CurrencyRepo(local, networkStatus, fetchTimestampDataSource) {
    override val type = CurrencyType.CRYPTO

    override suspend fun fetchRemote(): List<CurrencyRate> =
        cryptoAPI.getCryptoRates().findUSDTPairs()

    override suspend fun getCurrencyName(): List<CurrencyName> =
        getCurrencyRate().map {
            CurrencyName(it.code, name = "")
        }

    // api returns pairs like ETHBTC, ETHBNB, ETHTRX, ETHUSDT
    // we only take USDT pairs
    private fun List<CryptoRateResponse>.findUSDTPairs() =
        mapNotNull { (code, price) ->
            if (code.takeLast(4) == "USDT") {
                CurrencyRate(code.dropLast(4), price)
            } else
                null
        }
}