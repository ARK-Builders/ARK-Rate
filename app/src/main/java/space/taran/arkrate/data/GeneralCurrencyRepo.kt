package space.taran.arkrate.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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


    override suspend fun codeToRate(): List<CurrencyRate> =
        currencyRepos.fold(emptyList()) { codeToRate, repo ->
            codeToRate + repo.codeToRate()
        }

    override suspend fun codeToCurrency(): Map<String, String> =
        currencyRepos.fold(emptyMap()) { codeToCurrency, repo ->
            codeToCurrency + repo.codeToCurrency()
        }
}