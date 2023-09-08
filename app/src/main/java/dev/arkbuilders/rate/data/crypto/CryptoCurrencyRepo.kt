package dev.arkbuilders.rate.data.crypto

import dev.arkbuilders.rate.data.CurrencyName
import dev.arkbuilders.rate.data.CurrencyRate
import dev.arkbuilders.rate.data.CurrencyRepo
import dev.arkbuilders.rate.data.CurrencyType
import dev.arkbuilders.rate.data.network.NetworkStatus
import dev.arkbuilders.rate.data.db.CurrencyRateLocalDataSource
import dev.arkbuilders.rate.data.db.FetchTimestampDataSource
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
        cryptoAPI.getCryptoRates()
            .map { CurrencyRate(type, it.symbol.uppercase(), it.current_price) }

    override suspend fun getCurrencyName(): List<CurrencyName> =
        getCurrencyRate().map {
            CurrencyName(it.code, name = "")
        }
}