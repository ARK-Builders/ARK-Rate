package dev.arkbuilders.rate.feature.quick.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.core.domain.model.CurrencyName
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.model.TimestampType
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.core.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.GetTopResultUseCase
import dev.arkbuilders.rate.core.domain.usecase.GroupReorderSwapUseCase
import dev.arkbuilders.rate.core.presentation.AppSharedFlow
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupOptionsSheetState
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRenameSheetState
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupReorderSheetState
import dev.arkbuilders.rate.feature.quick.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
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
import timber.log.Timber
import java.time.OffsetDateTime

data class QuickScreenPage(
    val group: Group,
    val pinned: List<PinnedQuickPair>,
    val notPinned: List<QuickPair>,
)

data class PairOptionsData(val pair: QuickPair)

data class QuickScreenState(
    val filter: String = "",
    val currencies: List<CurrencyName> = emptyList(),
    val frequent: List<CurrencyName> = emptyList(),
    val topResults: List<CurrencyName> = emptyList(),
    val pages: List<QuickScreenPage> = emptyList(),
    val pairOptionsData: PairOptionsData? = null,
    val editGroupReorderSheetState: EditGroupReorderSheetState? = null,
    val editGroupOptionsSheetState: EditGroupOptionsSheetState? = null,
    val editGroupRenameSheetState: EditGroupRenameSheetState? = null,
    val initialized: Boolean = false,
    val noInternet: Boolean = false,
)

sealed class QuickScreenEffect {
    data class ShowSnackbarAdded(
        val visuals: NotifyAddedSnackbarVisuals,
    ) : QuickScreenEffect()

    data class ShowRemovedSnackbar(val pair: QuickPair) : QuickScreenEffect()

    data object NavigateBack : QuickScreenEffect()
}

class QuickViewModel(
    private val currencyRepo: CurrencyRepo,
    private val quickRepo: QuickRepo,
    private val timestampRepo: TimestampRepo,
    private val groupRepo: GroupRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val groupReorderSwapUseCase: GroupReorderSwapUseCase,
    private val getTopResultUseCase: GetTopResultUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModel(), ContainerHost<QuickScreenState, QuickScreenEffect> {
    override val container: Container<QuickScreenState, QuickScreenEffect> =
        container(QuickScreenState())

    init {
        analyticsManager.trackScreen("QuickScreen")

        intent {
            if (currencyRepo.isRatesAvailable().not()) {
                reduce {
                    state.copy(noInternet = true)
                }
                return@intent
            }

            init()
        }
    }

    private fun init() =
        intent {
            AppSharedFlow.ShowAddedSnackbarQuick.flow.onEach { visuals ->
                postSideEffect(QuickScreenEffect.ShowSnackbarAdded(visuals))
            }.launchIn(viewModelScope)

            quickRepo.allFlow().drop(1).onEach { quick ->
                intent {
                    val pages = mapPairsToPages(quick)
                    reduce {
                        state.copy(
                            pages = pages,
                        )
                    }
                }
            }.launchIn(viewModelScope)

            groupRepo.allFlow(GroupFeatureType.Quick).drop(1).onEach {
                intent {
                    val pages = mapPairsToPages(quickRepo.getAll())
                    reduce {
                        state.copy(
                            pages = pages,
                        )
                    }
                }
            }.launchIn(viewModelScope)

            val allCurrencies = currencyRepo.getCurrencyNameUnsafe()
            calcFrequentCurrUseCase.flow().drop(1).onEach {
                val frequent =
                    calcFrequentCurrUseCase.invoke()
                        .map { currencyRepo.nameByCodeUnsafe(it) }
                val topResults = getTopResultUseCase()
                reduce {
                    state.copy(
                        frequent = frequent,
                        topResults = topResults,
                    )
                }
            }.launchIn(viewModelScope)

            val frequent =
                calcFrequentCurrUseCase()
                    .map { currencyRepo.nameByCodeUnsafe(it) }
            val topResults = getTopResultUseCase()
            val pages = mapPairsToPages(quickRepo.getAll())
            reduce {
                state.copy(
                    currencies = allCurrencies,
                    frequent = frequent,
                    topResults = topResults,
                    pages = pages,
                    initialized = true,
                )
            }
        }

    fun onRefreshClick() =
        intent {
            reduce { state.copy(noInternet = false) }
            if (currencyRepo.isRatesAvailable()) {
                init()
            } else {
                reduce { state.copy(noInternet = true) }
            }
        }

    fun onShowGroupOptions(pair: QuickPair) =
        intent {
            reduce { state.copy(pairOptionsData = PairOptionsData(pair)) }
        }

    fun onHideOptions() =
        intent {
            reduce { state.copy(pairOptionsData = null) }
        }

    fun onPin(pair: QuickPair) =
        intent {
            val newPair = pair.copy(pinnedDate = OffsetDateTime.now())
            quickRepo.insert(newPair)
        }

    fun onUnpin(pair: QuickPair) =
        intent {
            val newPair = pair.copy(pinnedDate = null)
            quickRepo.insert(newPair)
        }

    fun onFilterChanged(filter: String) =
        blockingIntent {
            reduce { state.copy(filter = filter) }
        }

    fun onDelete(pair: QuickPair) =
        intent {
            val deleted = quickRepo.delete(pair.id)
            if (deleted) {
                postSideEffect(QuickScreenEffect.ShowRemovedSnackbar(pair))
            }
        }

    fun undoDelete(pair: QuickPair) =
        intent {
            quickRepo.insert(pair)
        }

    fun onBackClick() =
        intent {
            if (state.filter.isNotEmpty()) {
                reduce {
                    state.copy(filter = "")
                }
            } else {
                postSideEffect(QuickScreenEffect.NavigateBack)
            }
        }

    private suspend fun mapPairsToPages(pairs: List<QuickPair>): List<QuickScreenPage> {
        val refreshDate = timestampRepo.getTimestamp(TimestampType.FetchRates)
        val pages =
            pairs
                .reversed()
                .groupBy { it.group }
                .map { (group, pairs) ->
                    val (pinned, notPinned) = pairs.partition { it.isPinned() }
                    val pinnedMapped = pinned.map { mapPairToPinned(it, refreshDate!!) }
                    val sortedPinned =
                        pinnedMapped.sortedByDescending { it.pair.pinnedDate }
                    val sortedNotPinned =
                        notPinned.sortedByDescending { it.calculatedDate }
                    QuickScreenPage(group, sortedPinned, sortedNotPinned)
                }
                .sortedByDescending { it.group.orderIndex }
        return pages
    }

    private suspend fun mapPairToPinned(
        pair: QuickPair,
        refreshDate: OffsetDateTime,
    ): PinnedQuickPair {
        val actualTo =
            pair.to.map { to ->
                val (amount, _) = convertUseCase.invoke(pair.from, pair.amount, to.code)
                amount
            }
        return PinnedQuickPair(pair, actualTo, refreshDate)
    }

    //region Group Management

    fun onShowGroupsReorder() =
        intent {
            val groups = groupRepo.getAllSorted(GroupFeatureType.Quick)
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
                GroupFeatureType.Quick,
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
            val groups = groupRepo.getAllSorted(GroupFeatureType.Quick)
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
        groupRepo.update(renamed, GroupFeatureType.Quick)
        val groups = groupRepo.getAllSorted(GroupFeatureType.Quick)
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

class QuickViewModelFactory @AssistedInject constructor(
    private val quickRepo: QuickRepo,
    private val currencyRepo: CurrencyRepo,
    private val timestampRepo: TimestampRepo,
    private val groupRepo: GroupRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val groupReorderSwapUseCase: GroupReorderSwapUseCase,
    private val getTopResultUseCase: GetTopResultUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuickViewModel(
            currencyRepo,
            quickRepo,
            timestampRepo,
            groupRepo,
            convertUseCase,
            calcFrequentCurrUseCase,
            groupReorderSwapUseCase,
            getTopResultUseCase,
            analyticsManager,
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(): QuickViewModelFactory
    }
}
