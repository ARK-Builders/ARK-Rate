package dev.arkbuilders.rate.data

import dev.arkbuilders.rate.data.crypto.CryptoCurrencyRepo
import dev.arkbuilders.rate.data.fiat.FiatCurrencyRepo
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.CurrencyName
import dev.arkbuilders.rate.data.model.CurrencyRate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralCurrencyRepo @Inject constructor(val fiatRepo: FiatCurrencyRepo,
        val cryptoRepo: CryptoCurrencyRepo) {
    private val currencyRepos = listOf(fiatRepo, cryptoRepo)

    suspend fun getCurrencyRate(): List<CurrencyRate> =
        currencyRepos.fold(emptyList()) { codeToRate, repo ->
            codeToRate + repo.getCurrencyRate()
        }

    suspend fun getCurrencyName(): List<CurrencyName> =
        currencyRepos.fold(emptyList()) { currencyName, repo ->
            currencyName + repo.getCurrencyName()
        }

    suspend fun currencyNameByCode(code: CurrencyCode): CurrencyName {
        return fiatRepo.getCurrencyRate().find { it.code == code }?.let {
            fiatRepo.currencyNameByCode(code)
        }
            ?: let {
                cryptoRepo.currencyNameByCode(code)
            }
    }
}