package dev.arkbuilders.rate.data.currency

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
): CurrencyRepo {
    private val currencyRepos = listOf(
        fiatDataSource,
        cryptoDataSource
    )

    override suspend fun currencyNameByCode(code: CurrencyCode): CurrencyName {
        return fiatDataSource.getCurrencyRate().find { it.code == code }?.let {
            fiatDataSource.currencyNameByCode(code)
        } ?: let {
            cryptoDataSource.currencyNameByCode(code)
        }
    }

    override suspend fun rateByCode(code: CurrencyCode): CurrencyRate =
        getCodeToCurrencyRate()[code]!!

    override suspend fun getCodeToCurrencyRate(): Map<CurrencyCode, CurrencyRate> =
        getCurrencyRate().map { it.code to it }.toMap()

    override suspend fun getCurrencyRate(): List<CurrencyRate> =
        currencyRepos.fold(emptyList()) { codeToRate, repo ->
            codeToRate + repo.getCurrencyRate()
        }

    override suspend fun getCurrencyName(): List<CurrencyName> =
        currencyRepos.fold(emptyList()) { currencyName, repo ->
            currencyName + repo.getCurrencyName()
        }
}