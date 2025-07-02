package dev.arkbuilders.rate.core.data.repo.currency

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.arkbuilders.rate.core.data.mapper.CryptoRateResponseMapper
import dev.arkbuilders.rate.core.data.network.api.CryptoAPI
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.domain.model.CurrencyRate
import dev.arkbuilders.rate.core.domain.model.CurrencyType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoCurrencyDataSource @Inject constructor(
    private val cryptoAPI: CryptoAPI,
    private val cryptoRateResponseMapper: CryptoRateResponseMapper,
) : CurrencyDataSource {
    override val currencyType = CurrencyType.CRYPTO

    override suspend fun fetchRemote(): Either<Throwable, List<CurrencyRate>> {
        return try {
            val response = cryptoAPI.getCryptoRates()
            cryptoRateResponseMapper.map(response).right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun getCurrencyInfo(): Map<CurrencyCode, CurrencyInfo> = emptyMap()
}
