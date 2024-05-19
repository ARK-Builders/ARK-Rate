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

private val stubCurrency = CurrencyAmount(0, "USD", 0.0)

data class AddCurrencyState(
    val currencies: List<CurrencyAmount> = listOf(stubCurrency),
    val group: String? = null,
    val availableGroups: List<String> = emptyList()
)

sealed class AddCurrencySideEffect {
    class NotifyCurrencyAdded(val code: CurrencyCode) : AddCurrencySideEffect()
    data object NavigateBack : AddCurrencySideEffect()
}

class AddCurrencyViewModel(
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo
) : ViewModel(), ContainerHost<AddCurrencyState, AddCurrencySideEffect> {

    override val container: Container<AddCurrencyState, AddCurrencySideEffect> =
        container(AddCurrencyState())

    init {
        AppSharedFlow.AddCurrencyAmount.flow.onEach { (selectedCode, selectedAmount) ->
            intent {
                reduce {
                    val newCurrencies = state.currencies.toMutableList().replace(
                        selectedAmount,
                        selectedAmount.copy(code = selectedCode)
                    )
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

    fun onAssetRemove(amount: CurrencyAmount) = intent {
        reduce {
            state.copy(currencies = state.currencies.filter { it != amount })
        }
    }

    fun onGroupSelect(group: String?) = intent {
        reduce { state.copy(group = group) }
    }

    fun onAssetAmountChange(amount: CurrencyAmount, input: String) = blockingIntent {
        reduce {
            state.copy(
                currencies = state.currencies.fastMap { listAmount ->
                    if (listAmount == amount) {
                        listAmount.copy(
                            amount = CurrUtils.validateInput(
                                amount.amount.toString(),
                                input
                            ).toDouble()
                        )
                    } else listAmount
                }
            )
        }
    }

    fun onAddAsset() = intent {
        val currencies = state.currencies.map { it.copy(group = state.group) }
        assetsRepo.setCurrencyAmountList(currencies)
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