package dev.arkbuilders.rate.data.repo.currency

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.model.CurrencyType
import dev.arkbuilders.rate.data.network.NetworkStatus
import dev.arkbuilders.rate.data.network.api.CryptoAPI
import dev.arkbuilders.rate.domain.repo.TimestampRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoCurrencyDataSource @Inject constructor(
    private val cryptoAPI: CryptoAPI,
    private val local: LocalCurrencyDataSource,
    private val networkStatus: NetworkStatus,
    private val timestampRepo: TimestampRepo
) : CurrencyDataSource(local, networkStatus, timestampRepo) {
    override val currencyType = CurrencyType.CRYPTO

    override suspend fun fetchRemote(): Either<Throwable, List<CurrencyRate>> {
        return try {
            cryptoAPI.getCryptoRates()
                .map { CurrencyRate(currencyType, it.symbol.uppercase(), it.current_price) }
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