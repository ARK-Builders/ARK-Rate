package dev.arkbuilders.rate.feature.quick.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.model.TimestampType
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.core.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.GroupReorderSwapUseCase
import dev.arkbuilders.rate.core.domain.usecase.SearchUseCase
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupOptionsSheetState
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRenameSheetState
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupReorderSheetState
import dev.arkbuilders.rate.feature.quick.domain.model.PinnedQuickCalculation
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.OffsetDateTime

data class QuickScreenPage(
    val group: Group,
    val pinned: List<PinnedQuickCalculation>,
    val notPinned: List<QuickCalculation>,
)

data class CalculationOptionsData(val calculation: QuickCalculation)

data class QuickScreenState(
    val filter: String = "",
    val currencies: List<CurrencyInfo> = emptyList(),
    val frequent: List<CurrencyInfo> = emptyList(),
    val topResultsFiltered: List<CurrencyInfo> = emptyList(),
    val pages: List<QuickScreenPage> = emptyList(),
    val calculationOptionsData: CalculationOptionsData? = null,
    val editGroupReorderSheetState: EditGroupReorderSheetState? = null,
    val editGroupOptionsSheetState: EditGroupOptionsSheetState? = null,
    val editGroupRenameSheetState: EditGroupRenameSheetState? = null,
    val initialized: Boolean = false,
)

sealed class QuickScreenEffect {
    data class ShowSnackbarAdded(
        val calculation: QuickCalculation,
    ) : QuickScreenEffect()

    data class ShowRemovedSnackbar(val calculation: QuickCalculation) : QuickScreenEffect()

    data class NavigateToEdit(val calc: QuickCalculation) : QuickScreenEffect()

    data class NavigateToReuse(val calc: QuickCalculation) : QuickScreenEffect()

    data object LaunchInAppReview : QuickScreenEffect()

    data class SelectTab(val groupId: Long) : QuickScreenEffect()

    data object NavigateToCalculationOnboarding : QuickScreenEffect()

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
    private val searchUseCase: SearchUseCase,
    private val analyticsManager: AnalyticsManager,
    private val prefs: Prefs,
) : ViewModel(), ContainerHost<QuickScreenState, QuickScreenEffect> {
    override val container: Container<QuickScreenState, QuickScreenEffect> =
        container(QuickScreenState())

    init {
        init()
    }

    private fun init() =
        intent {
            quickRepo.allFlow().drop(1).onEach { quick ->
                intent {
                    val pages = mapCalculationsToPages(quick)
                    reduce {
                        state.copy(
                            pages = pages,
                        )
                    }
                }
            }.launchIn(viewModelScope)

            groupRepo.allFlow(GroupFeatureType.Quick).drop(1).onEach {
                intent {
                    val pages = mapCalculationsToPages(quickRepo.getAll())
                    reduce {
                        state.copy(
                            pages = pages,
                        )
                    }
                }
            }.launchIn(viewModelScope)

            val allCurrencies = currencyRepo.getCurrencyInfo()
            calcFrequentCurrUseCase.flow().drop(1).onEach {
                val frequent =
                    calcFrequentCurrUseCase.invoke()
                        .map { currencyRepo.infoByCode(it) }
                reduce {
                    state.copy(
                        frequent = frequent,
                    )
                }
            }.launchIn(viewModelScope)

            val frequent =
                calcFrequentCurrUseCase()
                    .map { currencyRepo.infoByCode(it) }
            val pages = mapCalculationsToPages(quickRepo.getAll())
            reduce {
                state.copy(
                    currencies = allCurrencies,
                    frequent = frequent,
                    pages = pages,
                    initialized = true,
                )
            }
        }

    fun onNavResultValue(newCalculationId: Long) =
        intent {
            if (prefs.get(PreferenceKey.IsOnboardingQuickCalculationCompleted).not()) {
                postSideEffect(QuickScreenEffect.NavigateToCalculationOnboarding)
                return@intent
            }

            val calculation = quickRepo.getById(newCalculationId) ?: return@intent
            val pages = mapCalculationsToPages(quickRepo.getAll())
            reduce {
                state.copy(
                    pages = pages,
                    filter = "",
                )
            }
            postSideEffect(QuickScreenEffect.LaunchInAppReview)
            postSideEffect(QuickScreenEffect.SelectTab(calculation.group.id))
            postSideEffect(QuickScreenEffect.ShowSnackbarAdded(calculation))
        }

    fun onNavResultCancelled() =
        intent {
            reduce {
                state.copy(
                    filter = "",
                )
            }
        }

    fun onShowGroupOptions(calculation: QuickCalculation) =
        intent {
            reduce { state.copy(calculationOptionsData = CalculationOptionsData(calculation)) }
        }

    fun onHideOptions() =
        intent {
            reduce { state.copy(calculationOptionsData = null) }
        }

    fun onPin(calculation: QuickCalculation) =
        intent {
            val newCalculation = calculation.copy(pinnedDate = OffsetDateTime.now())
            quickRepo.insert(newCalculation)
        }

    fun onUnpin(calculation: QuickCalculation) =
        intent {
            val newCalculation = calculation.copy(pinnedDate = null)
            quickRepo.insert(newCalculation)
        }

    fun onEdit(calc: QuickCalculation) =
        intent {
            postSideEffect(QuickScreenEffect.NavigateToEdit(calc))
        }

    fun onReuse(calc: QuickCalculation) =
        intent {
            postSideEffect(QuickScreenEffect.NavigateToReuse(calc))
        }

    fun onFilterChanged(filter: String) =
        blockingIntent {
            reduce {
                state.copy(
                    filter = filter,
                    topResultsFiltered =
                        searchUseCase(
                            state.currencies,
                            state.frequent.map { it.code },
                            filter,
                        ),
                )
            }
        }

    fun onDelete(calculation: QuickCalculation) =
        intent {
            val deleted = quickRepo.delete(calculation.id)
            if (deleted) {
                postSideEffect(QuickScreenEffect.ShowRemovedSnackbar(calculation))
            }
        }

    fun undoDelete(calculation: QuickCalculation) =
        intent {
            quickRepo.insert(calculation)
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

    private suspend fun mapCalculationsToPages(
        calculations: List<QuickCalculation>,
    ): List<QuickScreenPage> {
        val refreshDate = timestampRepo.getTimestamp(TimestampType.FetchRates)
        val groups = groupRepo.getAllSorted(GroupFeatureType.Quick)
        val pages =
            groups.map { group ->
                val filteredCalculations =
                    calculations
                        .filter { it.group.id == group.id }
                        .toMutableList()
                filteredCalculations.reverse()
                val (pinned, notPinned) = filteredCalculations.partition { it.isPinned() }
                val pinnedMapped =
                    pinned.map {
                        mapCalculationToPinned(it, refreshDate!!)
                    }
                val sortedPinned =
                    pinnedMapped.sortedByDescending { it.calculation.pinnedDate }
                val sortedNotPinned =
                    notPinned.sortedByDescending { it.calculatedDate }
                QuickScreenPage(group, sortedPinned, sortedNotPinned)
            }.filter { it.pinned.isNotEmpty() || it.notPinned.isNotEmpty() }
        return pages
    }

    private suspend fun mapCalculationToPinned(
        calculation: QuickCalculation,
        refreshDate: OffsetDateTime,
    ): PinnedQuickCalculation {
        val actualTo =
            calculation.to.map { to ->
                val (amount, _) =
                    convertUseCase.invoke(
                        calculation.from,
                        calculation.amount,
                        to.code,
                    )
                amount
            }
        return PinnedQuickCalculation(calculation, actualTo, refreshDate)
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
    private val searchUseCase: SearchUseCase,
    private val analyticsManager: AnalyticsManager,
    private val prefs: Prefs,
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
            searchUseCase,
            analyticsManager,
            prefs,
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(): QuickViewModelFactory
    }
}
