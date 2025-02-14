package dev.arkbuilders.rate.feature.portfolio.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.AmountStr
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.toBigDecimalArk
import dev.arkbuilders.rate.core.domain.usecase.GetGroupByIdOrCreateDefaultUseCase
import dev.arkbuilders.rate.core.presentation.AppSharedFlow
import dev.arkbuilders.rate.feature.portfolio.domain.model.Asset
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.feature.portfolio.domain.usecase.AddNewAssetsUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber

data class AddAssetState(
    val currencies: List<AmountStr> = listOf(AmountStr("USD", "")),
    val group: Group = Group.empty(),
    val availableGroups: List<Group> = emptyList(),
)

sealed class AddAssetSideEffect {
    class NotifyAssetAdded(val amounts: List<Asset>) :
        AddAssetSideEffect()

    data object NavigateBack : AddAssetSideEffect()

    data class NavigateSearchAdd(val prohibitedCodes: List<CurrencyCode>) : AddAssetSideEffect()

    data class NavigateSearchSet(val index: Int, val prohibitedCodes: List<CurrencyCode>) :
        AddAssetSideEffect()
}

class AddAssetViewModel(
    private val groupId: Long?,
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val groupRepo: GroupRepo,
    private val getGroupByIdOrCreateDefaultUseCase: GetGroupByIdOrCreateDefaultUseCase,
    private val analyticsManager: AnalyticsManager,
    private val addNewAssetsUseCase: AddNewAssetsUseCase,
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
            val group = getGroupByIdOrCreateDefaultUseCase(groupId, GroupFeatureType.Portfolio)
            val groups = groupRepo.getAllSorted(GroupFeatureType.Portfolio)
            reduce {
                state.copy(group = group, availableGroups = groups)
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

    fun onGroupCreate(name: String) =
        intent {
            val group = groupRepo.new(name, GroupFeatureType.Portfolio)
            reduce {
                state.copy(group = group)
            }
        }

    fun onGroupSelect(group: Group) =
        intent {
            reduce { state.copy(group = group) }
        }

    // weird bug
    // onAssetValueChange can be called after onAssetRemove with removed pos and empty input
    // this only happens if you try to type wrong characters(e.g. whitespace, "-") before deleting
    // and element being removed is last one in list
    fun onAssetValueChange(
        pos: Int,
        input: String,
    ) = blockingIntent {
        val newCurrencies = state.currencies.toMutableList()
        val old =
            newCurrencies.getOrNull(pos) ?: let {
                Timber.w("onAssetValueChange called with nonexistent pos")
                return@blockingIntent
            }
        val validatedAmount = CurrUtils.validateInput(old.value, input)
        newCurrencies[pos] = newCurrencies[pos].copy(value = validatedAmount)
        reduce {
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
            addNewAssetsUseCase(currencies)
            codeUseStatRepo.codesUsed(*currencies.map { it.code }.toTypedArray())
            postSideEffect(AddAssetSideEffect.NotifyAssetAdded(currencies))
            postSideEffect(AddAssetSideEffect.NavigateBack)
        }

    fun onSetCode(index: Int) =
        intent {
            val prohibitedCodes = state.currencies.map { it.code }.toMutableList()
            prohibitedCodes.removeAt(index)
            postSideEffect(AddAssetSideEffect.NavigateSearchSet(index, prohibitedCodes))
        }

    fun onAddCode() =
        intent {
            val prohibitedCodes = state.currencies.map { it.code }
            postSideEffect(AddAssetSideEffect.NavigateSearchAdd(prohibitedCodes))
        }
}

class AddAssetViewModelFactory @AssistedInject constructor(
    @Assisted private val groupId: Long?,
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val groupRepo: GroupRepo,
    private val getGroupByIdOrCreateDefaultUseCase: GetGroupByIdOrCreateDefaultUseCase,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val analyticsManager: AnalyticsManager,
    private val addNewAssetsUseCase: AddNewAssetsUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddAssetViewModel(
            groupId,
            assetsRepo,
            currencyRepo,
            codeUseStatRepo,
            groupRepo,
            getGroupByIdOrCreateDefaultUseCase,
            analyticsManager,
            addNewAssetsUseCase,
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted groupId: Long?,
        ): AddAssetViewModelFactory
    }
}
