package dev.arkbuilders.rate.presentation.addcurrency

import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.data.model.CurrencyAmount
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.db.AssetsRepo
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.toDoubleSafe
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import dev.arkbuilders.rate.utils.replace
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import javax.inject.Singleton

private val stubCurrency = "USD" to ""

data class AddCurrencyState(
    val currencies: List<Pair<CurrencyCode, String>> = listOf(stubCurrency),
    val group: String? = null,
    val availableGroups: List<String> = emptyList()
)

sealed class AddCurrencySideEffect {
    class NotifyAssetAdded(val amounts: List<CurrencyAmount>) :
        AddCurrencySideEffect()

    data object NavigateBack : AddCurrencySideEffect()
}

class AddCurrencyViewModel(
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo
) : ViewModel(), ContainerHost<AddCurrencyState, AddCurrencySideEffect> {

    override val container: Container<AddCurrencyState, AddCurrencySideEffect> =
        container(AddCurrencyState())

    init {
        AppSharedFlow.AddCurrencyAmount.flow.onEach { (pos, selectedCode) ->
            intent {
                reduce {
                    val newCurrencies = state.currencies.toMutableList()
                    newCurrencies[pos] =
                        newCurrencies[pos].copy(first = selectedCode)
                    state.copy(currencies = newCurrencies)
                }
            }
        }.launchIn(viewModelScope)

        intent {
            val groups =
                assetsRepo.allCurrencyAmount().mapNotNull { it.group }.distinct()
            reduce {
                state.copy(availableGroups = groups)
            }
        }
    }

    fun onNewCurrencyClick() = intent {
        reduce {
            state.copy(currencies = state.currencies + stubCurrency)
        }
    }

    fun onAssetRemove(removeIndex: Int) = intent {
        reduce {
            state.copy(
                currencies = state.currencies
                    .filterIndexed { index, _ -> index != removeIndex }
            )
        }
    }

    fun onGroupSelect(group: String?) = intent {
        reduce { state.copy(group = group) }
    }

    fun onAssetAmountChange(pos: Int, input: String) = blockingIntent {
        reduce {
            val newCurrencies = state.currencies.toMutableList()
            val old = newCurrencies[pos]
            val validatedAmount = CurrUtils.validateInput(old.second, input)
            newCurrencies[pos] = newCurrencies[pos].copy(second = validatedAmount)
            state.copy(currencies = newCurrencies)
        }
    }

    fun onAddAsset() = intent {
        val currencies = state.currencies.map {
            CurrencyAmount(code = it.first, amount = it.second.toDoubleSafe(), group = state.group)
        }
        assetsRepo.setCurrencyAmountList(currencies)
        postSideEffect(AddCurrencySideEffect.NotifyAssetAdded(currencies))
        postSideEffect(AddCurrencySideEffect.NavigateBack)
    }
}

@Singleton
class AddCurrencyViewModelFactory @Inject constructor(
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddCurrencyViewModel(assetsRepo, currencyRepo) as T
    }
}