package dev.arkbuilders.rate.core.data.repo.currency

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.arkbuilders.rate.core.data.network.api.CryptoAPI
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyName
import dev.arkbuilders.rate.core.domain.model.CurrencyRate
import dev.arkbuilders.rate.core.domain.model.CurrencyType
import java.math.BigDecimal
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
                .map {
                    CurrencyRate(
                        currencyType,
                        it.symbol.uppercase(),
                        BigDecimal.valueOf(it.current_price),
                    )
                }
                .right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun getCurrencyName(): Map<CurrencyCode, CurrencyName> = emptyMap()
}