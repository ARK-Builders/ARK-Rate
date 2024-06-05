package dev.arkbuilders.rate.data.currency

import arrow.core.Either
import arrow.core.left
import arrow.core.leftWiden
import arrow.core.right
import dev.arkbuilders.rate.data.currency.crypto.CryptoCurrencyDataSource
import dev.arkbuilders.rate.data.currency.fiat.FiatCurrencyDataSource
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.CurrencyRate
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepoImpl @Inject constructor(
    val fiatDataSource: FiatCurrencyDataSource,
    val cryptoDataSource: CryptoCurrencyDataSource
) : CurrencyRepo {

    override suspend fun getCurrencyRate(): Either<Throwable, List<CurrencyRate>> {
        val crypto = cryptoDataSource.getCurrencyRate()
        val fiat = fiatDataSource.getCurrencyRate()
        if (crypto.isLeft() || fiat.isLeft()) {
            return crypto
        }

        return (fiat.getOrNull()!! + crypto.getOrNull()!!).right()
    }

    override suspend fun getCurrencyName(): Either<Throwable, List<CurrencyName>> {
        val crypto = cryptoDataSource.getCurrencyName()
        val fiat = fiatDataSource.getCurrencyName()
        if (crypto.isLeft() || fiat.isLeft()) {
            return crypto
        }

        return (fiat.getOrNull()!! + crypto.getOrNull()!!).right()
    }

    override suspend fun nameByCode(
        code: CurrencyCode
    ): Either<Throwable, CurrencyName> = getCurrencyName().fold(
        ifLeft = {
            it.left()
        },
        ifRight = { names ->
            val name = names.find { name -> name.code == code }
            return name?.right() ?: IllegalStateException().left()
        }
    )

    override suspend fun rateByCode(code: CurrencyCode): Either<Throwable, CurrencyRate> =
        getCodeToCurrencyRate().map { codeToRate -> codeToRate[code]!! }

    override suspend fun getCodeToCurrencyRate(): Either<Throwable, Map<CurrencyCode, CurrencyRate>> =
        getCurrencyRate().map { rates -> rates.associateBy { it.code } }
}