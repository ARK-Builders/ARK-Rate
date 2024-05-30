package dev.arkbuilders.rate.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.data.currency.CurrencyRepoImpl
import dev.arkbuilders.rate.data.db.PortfolioRepoImpl
import dev.arkbuilders.rate.domain.model.CurrencyAmount
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.data.preferences.PrefsImpl
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.domain.repo.PreferenceKey
import dev.arkbuilders.rate.domain.repo.Prefs
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import javax.inject.Singleton

data class PortfolioScreenState(
    val baseCode: CurrencyCode = "USD",
    val groupToPortfolioAmount: Map<String?, List<PortfolioDisplayAmount>> = emptyMap(),
)

class PortfolioDisplayAmount(
    val amount: CurrencyAmount,
    val baseAmount: CurrencyAmount,
    val ratioToBase: Double
)

sealed class PortfolioScreenEffect

class PortfolioViewModel(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val prefs: Prefs
) : ViewModel(), ContainerHost<PortfolioScreenState, PortfolioScreenEffect> {

    override val container: Container<PortfolioScreenState, PortfolioScreenEffect> =
        container(PortfolioScreenState())

    init {
        init()

        prefs.flow(PreferenceKey.BaseCurrencyCode).drop(1).onEach {
            init()
        }.launchIn(viewModelScope)

        assetsRepo.allCurrencyAmountFlow().drop(1).onEach {
            init()
        }.launchIn(viewModelScope)
    }

    fun onAssetRemove(amount: CurrencyAmount) = intent {
        assetsRepo.removeCurrency(amount.id)
    }

    private fun init() = intent {
        val baseCode = prefs.get(PreferenceKey.BaseCurrencyCode)
        val list = assetsRepo.allCurrencyAmount()
        val groups = list.groupBy(keySelector = { it.group })
        val groupToPortfolioAmount = groups.mapValues {
            assetToPortfolioDisplayAmount(
                baseCode,
                it.value
            )
        }
        reduce {
            state.copy(baseCode, groupToPortfolioAmount)
        }
    }

    private suspend fun assetToPortfolioDisplayAmount(
        baseCode: CurrencyCode,
        list: List<CurrencyAmount>
    ): List<PortfolioDisplayAmount> {
        val rates = currencyRepo.getCodeToCurrencyRate()
        return list.map { amount ->
            val baseRate = rates[amount.code]!!.rate / rates[baseCode]!!.rate
            val baseAmount =
                CurrencyAmount(code = baseCode, amount = amount.amount * baseRate)
            PortfolioDisplayAmount(amount, baseAmount, baseRate)
        }
    }
}

@Singleton
class PortfolioViewModelFactory @Inject constructor(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val prefs: Prefs
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PortfolioViewModel(assetsRepo, currencyRepo, prefs) as T
    }
}