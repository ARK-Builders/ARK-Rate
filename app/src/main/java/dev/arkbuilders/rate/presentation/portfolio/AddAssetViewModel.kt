package dev.arkbuilders.rate.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.domain.model.Asset
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.data.toDoubleSafe
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
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

private val stubCurrency = "USD" to ""

data class AddAssetState(
    val currencies: List<Pair<CurrencyCode, String>> = listOf(stubCurrency),
    val group: String? = null,
    val availableGroups: List<String> = emptyList()
)

sealed class AddAssetSideEffect {
    class NotifyAssetAdded(val amounts: List<Asset>) :
        AddAssetSideEffect()

    data object NavigateBack : AddAssetSideEffect()
}

class AddAssetViewModel(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo
) : ViewModel(), ContainerHost<AddAssetState, AddAssetSideEffect> {

    override val container: Container<AddAssetState, AddAssetSideEffect> =
        container(AddAssetState())

    init {
        AppSharedFlow.SetAssetCode.flow.onEach { (pos, selectedCode) ->
            intent {
                reduce {
                    val newCurrencies = state.currencies.toMutableList()
                    newCurrencies[pos] =
                        newCurrencies[pos].copy(first = selectedCode)
                    state.copy(currencies = newCurrencies)
                }
            }
        }.launchIn(viewModelScope)

        AppSharedFlow.AddAsset.flow.onEach { code ->
            intent {
                reduce { state.copy(currencies = state.currencies + (code to "")) }
            }
        }.launchIn(viewModelScope)

        intent {
            val groups =
                assetsRepo.allAssets().mapNotNull { it.group }.distinct()
            reduce {
                state.copy(availableGroups = groups)
            }
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

    fun onAssetValueChange(pos: Int, input: String) = blockingIntent {
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
            Asset(code = it.first, value = it.second.toDoubleSafe(), group = state.group)
        }
        assetsRepo.setAssetsList(currencies)
        postSideEffect(AddAssetSideEffect.NotifyAssetAdded(currencies))
        postSideEffect(AddAssetSideEffect.NavigateBack)
    }
}

@Singleton
class AddAssetViewModelFactory @Inject constructor(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddAssetViewModel(assetsRepo, currencyRepo) as T
    }
}