package dev.arkbuilders.rate.feature.portfolio.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.AmountStr
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.toBigDecimalArk
import dev.arkbuilders.rate.core.presentation.AppSharedFlow
import dev.arkbuilders.rate.feature.portfolio.di.PortfolioScope
import dev.arkbuilders.rate.feature.portfolio.domain.model.Asset
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo
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

data class AddAssetState(
    val currencies: List<AmountStr> = listOf(AmountStr("USD", "")),
    val group: String? = null,
    val availableGroups: List<String> = emptyList(),
)

sealed class AddAssetSideEffect {
    class NotifyAssetAdded(val amounts: List<Asset>) :
        AddAssetSideEffect()

    data object NavigateBack : AddAssetSideEffect()
}

class AddAssetViewModel(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val analyticsManager: AnalyticsManager,
) : ViewModel(), ContainerHost<AddAssetState, AddAssetSideEffect> {
    override val container: Container<AddAssetState, AddAssetSideEffect> =
        container(AddAssetState())

    init {
        analyticsManager.trackScreen("AddAssetScreen")

        AppSharedFlow.SetAssetCode.flow.onEach { (pos, selectedCode) ->
            intent {
                reduce {
                    val newCurrencies = state.currencies.toMutableList()
                    newCurrencies[pos] =
                        newCurrencies[pos].copy(code = selectedCode)
                    state.copy(currencies = newCurrencies)
                }
            }
        }.launchIn(viewModelScope)

        AppSharedFlow.AddAsset.flow.onEach { code ->
            intent {
                reduce {
                    state.copy(
                        currencies =
                            state.currencies +
                                AmountStr(
                                    code,
                                    "",
                                ),
                    )
                }
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

    fun onAssetRemove(removeIndex: Int) =
        intent {
            reduce {
                state.copy(
                    currencies =
                        state.currencies
                            .filterIndexed { index, _ -> index != removeIndex },
                )
            }
        }

    fun onGroupSelect(group: String?) =
        intent {
            reduce { state.copy(group = group) }
        }

    fun onAssetValueChange(
        pos: Int,
        input: String,
    ) = blockingIntent {
        reduce {
            val newCurrencies = state.currencies.toMutableList()
            val old = newCurrencies[pos]
            val validatedAmount = CurrUtils.validateInput(old.value, input)
            newCurrencies[pos] = newCurrencies[pos].copy(value = validatedAmount)
            state.copy(currencies = newCurrencies)
        }
    }

    fun onAddAsset() =
        intent {
            val currencies =
                state.currencies.map {
                    Asset(
                        code = it.code,
                        value = it.value.toBigDecimalArk(),
                        group = state.group,
                    )
                }
            assetsRepo.setAssetsList(currencies)
            codeUseStatRepo.codesUsed(*currencies.map { it.code }.toTypedArray())
            postSideEffect(AddAssetSideEffect.NotifyAssetAdded(currencies))
            postSideEffect(AddAssetSideEffect.NavigateBack)
        }
}

@PortfolioScope
class AddAssetViewModelFactory @Inject constructor(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddAssetViewModel(
            assetsRepo,
            currencyRepo,
            codeUseStatRepo,
            analyticsManager,
        ) as T
    }
}
