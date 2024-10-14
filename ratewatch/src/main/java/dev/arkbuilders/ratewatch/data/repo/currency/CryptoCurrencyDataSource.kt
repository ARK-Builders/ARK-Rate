package dev.arkbuilders.ratewatch.data.repo.currency

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.arkbuilders.ratewatch.data.network.api.CryptoAPI
import dev.arkbuilders.ratewatch.domain.model.CurrencyCode
import dev.arkbuilders.ratewatch.domain.model.CurrencyName
import dev.arkbuilders.ratewatch.domain.model.CurrencyRate
import dev.arkbuilders.ratewatch.domain.model.CurrencyType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoCurrencyDataSource @Inject constructor(
    private val cryptoAPI: CryptoAPI,
) : CurrencyDataSource {
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

    override suspend fun getCurrencyName(): Map<CurrencyCode, CurrencyName> = emptyMap()
}
