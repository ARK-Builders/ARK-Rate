package dev.arkbuilders.rate.presentation.quick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.data.db.QuickRepo
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.QuickPair
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
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

data class AddQuickScreenState(
    val currencies: List<CurrencyCode> = listOf("USD"),
    val amount: String = "",
    val group: String? = null,
    val availableGroups: List<String> = emptyList()
)

sealed class AddQuickScreenEffect {
    data class NotifyPairAdded(val pair: QuickPair) : AddQuickScreenEffect()
    data object NavigateBack : AddQuickScreenEffect()
}

class AddQuickViewModel(
    private val quickRepo: QuickRepo
) : ViewModel(), ContainerHost<AddQuickScreenState, AddQuickScreenEffect> {

    override val container: Container<AddQuickScreenState, AddQuickScreenEffect> =
        container(AddQuickScreenState())

    init {
        AppSharedFlow.AddQuick.flow.onEach { (index, code) ->
            intent {
                reduce {
                    val newCurrencies = state.currencies.toMutableList()
                    newCurrencies[index] = code
                    state.copy(currencies = newCurrencies)
                }
            }
        }.launchIn(viewModelScope)

        intent {
            val groups =
                quickRepo.getAll().mapNotNull { it.group }.distinct()
            reduce {
                state.copy(availableGroups = groups)
            }
        }
    }

    fun onNewCurrencyClick() = intent {
        reduce {
            state.copy(currencies = state.currencies + "USD")
        }
    }

    fun onCurrencyRemove(removeIndex: Int) = intent {
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

    fun onAssetAmountChange(input: String) = blockingIntent {
        reduce {
            state.copy(
                amount = CurrUtils.validateInput(state.amount, input)
            )
        }
    }

    fun onAddQuickPair() = intent {
        val quick = QuickPair(
            id = 0,
            from = state.currencies.first(),
            amount = state.amount.toDouble(),
            to = state.currencies - state.currencies.first(),
            group = state.group
        )
        quickRepo.insert(quick)
        postSideEffect(AddQuickScreenEffect.NotifyPairAdded(quick))
        postSideEffect(AddQuickScreenEffect.NavigateBack)
    }

}

@Singleton
class AddQuickViewModelFactory @Inject constructor(
    private val quickRepo: QuickRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddQuickViewModel(quickRepo) as T
    }
}