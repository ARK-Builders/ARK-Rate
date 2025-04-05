package dev.arkbuilders.rate.feature.portfolio.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.GroupReorderSwapUseCase
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupOptionsSheetState
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRenameSheetState
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupReorderSheetState
import dev.arkbuilders.rate.feature.portfolio.di.PortfolioScope
import dev.arkbuilders.rate.feature.portfolio.domain.model.Asset
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.feature.portfolio.presentation.model.AddAssetsNavResult
import dev.arkbuilders.rate.feature.portfolio.presentation.model.NavAsset
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.math.BigDecimal
import javax.inject.Inject

data class PortfolioScreenState(
    val filter: String = "",
    val baseCode: CurrencyCode = "USD",
    val pages: List<PortfolioScreenPage> = emptyList(),
    val editGroupReorderSheetState: EditGroupReorderSheetState? = null,
    val editGroupOptionsSheetState: EditGroupOptionsSheetState? = null,
    val editGroupRenameSheetState: EditGroupRenameSheetState? = null,
    val initialized: Boolean = false,
)

data class PortfolioScreenPage(
    val group: Group,
    val assets: List<PortfolioDisplayAsset>,
)

data class PortfolioDisplayAsset(
    val asset: Asset,
    val baseAmount: Amount,
    val ratioToBase: BigDecimal,
)

sealed class PortfolioScreenEffect {
    class ShowSnackbarAdded(val assets: List<NavAsset>) : PortfolioScreenEffect()

    data class ShowRemovedSnackbar(val asset: Asset) : PortfolioScreenEffect()

    data class SelectTab(val groupId: Long) : PortfolioScreenEffect()

    data object NavigateBack : PortfolioScreenEffect()
}

class PortfolioViewModel(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val groupRepo: GroupRepo,
    private val prefs: Prefs,
    private val convertUseCase: ConvertWithRateUseCase,
    private val groupReorderSwapUseCase: GroupReorderSwapUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModel(), ContainerHost<PortfolioScreenState, PortfolioScreenEffect> {
    override val container: Container<PortfolioScreenState, PortfolioScreenEffect> =
        container(PortfolioScreenState())

    init {
        analyticsManager.trackScreen("PortfolioScreen")

        init()
    }

    private fun init() =
        intent {
            initPages()

            prefs.flow(PreferenceKey.BaseCurrencyCode).drop(1).onEach {
                initPages()
            }.launchIn(viewModelScope)

            assetsRepo.allAssetsFlow().drop(1).onEach {
                initPages()
            }.launchIn(viewModelScope)

            groupRepo.allFlow(GroupFeatureType.Portfolio).drop(1).onEach {
                initPages()
            }.launchIn(viewModelScope)
        }

    fun onReturnFromAddScreen(result: AddAssetsNavResult) =
        intent {
            if (result.added.isNotEmpty()) {
                initPages()
                postSideEffect(PortfolioScreenEffect.SelectTab(result.added.first().groupId))
                postSideEffect(PortfolioScreenEffect.ShowSnackbarAdded(result.added.toList()))
            }
        }

    fun onAssetRemove(asset: Asset) =
        intent {
            val deleted = assetsRepo.removeAsset(asset.id)
            if (deleted) {
                postSideEffect(PortfolioScreenEffect.ShowRemovedSnackbar(asset))
            }
        }

    fun undoDelete(asset: Asset) =
        intent {
            assetsRepo.setAsset(asset)
        }

    fun onFilterChange(filter: String) =
        blockingIntent {
            reduce { state.copy(filter = filter) }
        }

    fun onBackClick() =
        intent {
            if (state.filter.isNotEmpty()) {
                reduce {
                    state.copy(filter = "")
                }
            } else {
                postSideEffect(PortfolioScreenEffect.NavigateBack)
            }
        }

    private fun initPages() =
        intent {
            val baseCode = prefs.get(PreferenceKey.BaseCurrencyCode)
            val assets = assetsRepo.allAssets().toMutableList()
            assets.reverse()
            val groups = groupRepo.getAllSorted(GroupFeatureType.Portfolio)
            val pages =
                groups.map { group ->
                    val filteredAssets = assets.filter { it.group.id == group.id }
                    val displayAssets =
                        assetToPortfolioDisplayAmount(
                            baseCode,
                            filteredAssets,
                        )
                    PortfolioScreenPage(group, displayAssets)
                }.filter { it.assets.isNotEmpty() }
            reduce {
                state.copy(baseCode = baseCode, pages = pages, initialized = true)
            }
        }

    private suspend fun assetToPortfolioDisplayAmount(
        baseCode: CurrencyCode,
        list: List<Asset>,
    ): List<PortfolioDisplayAsset> {
        val rates = currencyRepo.getCodeToCurrencyRate()
        return list.map { asset ->
            val (baseAmount, rate) =
                convertUseCase(
                    asset.code,
                    asset.value,
                    toCode = baseCode,
                    rates,
                )
            PortfolioDisplayAsset(asset, baseAmount, rate)
        }
    }

    //region Group Management

    fun onShowGroupsReorder() =
        intent {
            val groups = groupRepo.getAllSorted(GroupFeatureType.Portfolio)
            reduce {
                state.copy(
                    editGroupReorderSheetState = EditGroupReorderSheetState(groups),
                )
            }
        }

    fun onSwapGroups(
        from: Int,
        to: Int,
    ) = intent {
        val newGroups =
            groupReorderSwapUseCase(
                state.editGroupReorderSheetState!!.groups,
                from,
                to,
                GroupFeatureType.Portfolio,
            )

        reduce {
            state.copy(
                editGroupReorderSheetState =
                    state.editGroupReorderSheetState?.copy(
                        groups = newGroups,
                    ),
            )
        }
    }

    fun onDismissGroupsReorder() =
        intent {
            reduce { state.copy(editGroupReorderSheetState = null) }
        }

    fun onShowGroupOptions(group: Group) =
        intent {
            reduce { state.copy(editGroupOptionsSheetState = EditGroupOptionsSheetState(group)) }
        }

    fun onGroupDelete(group: Group) =
        intent {
            groupRepo.delete(group.id)
            val groups = groupRepo.getAllSorted(GroupFeatureType.Portfolio)
            reduce {
                state.copy(
                    editGroupReorderSheetState =
                        state.editGroupReorderSheetState!!.copy(
                            groups = groups,
                        ),
                    editGroupOptionsSheetState = null,
                    editGroupRenameSheetState = null,
                )
            }
        }

    fun onDismissGroupOptions() =
        intent {
            reduce { state.copy(editGroupOptionsSheetState = null) }
        }

    fun onShowGroupRename(group: Group) =
        intent {
            reduce { state.copy(editGroupRenameSheetState = EditGroupRenameSheetState(group)) }
        }

    fun onGroupRename(
        group: Group,
        newName: String,
    ) = intent {
        val renamed = group.copy(name = newName)
        groupRepo.update(renamed, GroupFeatureType.Portfolio)
        val groups = groupRepo.getAllSorted(GroupFeatureType.Portfolio)
        reduce {
            state.copy(
                editGroupReorderSheetState =
                    state.editGroupReorderSheetState!!.copy(
                        groups = groups,
                    ),
                editGroupOptionsSheetState = null,
                editGroupRenameSheetState = null,
            )
        }
    }

    fun onDismissGroupRename() =
        intent {
            reduce { state.copy(editGroupRenameSheetState = null) }
        }

    //endregion
}

@PortfolioScope
class PortfolioViewModelFactory @Inject constructor(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val prefs: Prefs,
    private val convertUseCase: ConvertWithRateUseCase,
    private val groupReorderSwapUseCase: GroupReorderSwapUseCase,
    private val analyticsManager: AnalyticsManager,
    private val groupRepo: GroupRepo,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PortfolioViewModel(
            assetsRepo,
            currencyRepo,
            groupRepo,
            prefs,
            convertUseCase,
            groupReorderSwapUseCase,
            analyticsManager,
        ) as T
    }
}
