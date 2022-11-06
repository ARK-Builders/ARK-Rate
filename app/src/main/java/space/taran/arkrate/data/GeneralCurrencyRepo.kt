package space.taran.arkrate.data

import space.taran.arkrate.data.crypto.CryptoCurrencyRepo
import space.taran.arkrate.data.fiat.FiatCurrencyRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralCurrencyRepo @Inject constructor(
    val fiatRepo: FiatCurrencyRepo,
    val cryptoRepo: CryptoCurrencyRepo
) : CurrencyRepo {
    private val currencyRepos = listOf(
        fiatRepo,
        cryptoRepo
    )

    override suspend fun getCurrencyRate(): List<CurrencyRate> =
        currencyRepos.fold(emptyList()) { codeToRate, repo ->
            codeToRate + repo.getCurrencyRate()
        }

    override suspend fun getCurrencyName(): List<CurrencyName> =
        currencyRepos.fold(emptyList()) { currencyName, repo ->
            currencyName + repo.getCurrencyName()
        }
}