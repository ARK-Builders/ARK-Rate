package dev.arkbuilders.rate.data.currency.crypto

import arrow.core.Either
import arrow.core.left
import arrow.core.right
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

    override suspend fun fetchRemote(): Either<Throwable, List<CurrencyRate>> {
        return try {
            cryptoAPI.getCryptoRates()
                .map { CurrencyRate(type, it.symbol.uppercase(), it.current_price) }
                .right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun getCurrencyName(): Either<Throwable, List<CurrencyName>> =
        getCurrencyRate().map { rates ->
            rates.map {
                CurrencyName(it.code, name = "")
            }
        }
}