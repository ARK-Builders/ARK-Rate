package dev.arkbuilders.rate.data.currency.crypto

import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.data.currency.CurrencyDataSource
import dev.arkbuilders.rate.domain.model.CurrencyType
import dev.arkbuilders.rate.data.network.NetworkStatus
import dev.arkbuilders.rate.data.db.CurrencyRateLocalDataSource
import dev.arkbuilders.rate.data.db.FetchTimestampDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoCurrencyDataSource @Inject constructor(
    private val cryptoAPI: CryptoAPI,
    private val local: CurrencyRateLocalDataSource,
    private val networkStatus: NetworkStatus,
    private val fetchTimestampDataSource: FetchTimestampDataSource
) : CurrencyDataSource(local, networkStatus, fetchTimestampDataSource) {
    override val type = CurrencyType.CRYPTO

    override suspend fun fetchRemote(): List<CurrencyRate> =
        cryptoAPI.getCryptoRates()
            .map { CurrencyRate(type, it.symbol.uppercase(), it.current_price) }

    override suspend fun getCurrencyName(): List<CurrencyName> =
        getCurrencyRate().map {
            CurrencyName(it.code, name = "")
        }

    override suspend fun currencyNameByCode(code: CurrencyCode) =
        CurrencyName(code, name = "")
}