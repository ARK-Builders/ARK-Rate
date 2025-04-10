package dev.arkbuilders.rate.feature.quick.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.AmountStr
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.model.toAmount
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.toBigDecimalArk
import dev.arkbuilders.rate.core.domain.toDoubleArk
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.GetGroupByIdOrCreateDefaultUseCase
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import dev.arkbuilders.rate.feature.search.presentation.SearchNavResult
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.OffsetDateTime

data class AddQuickScreenState(
    val quickPairId: Long? = null,
    val currencies: List<AmountStr> = listOf(AmountStr("USD", "")),
    val group: Group = Group.empty(),
    val availableGroups: List<Group> = emptyList(),
    val finishEnabled: Boolean = false,
    val initialized: Boolean = false,
)

sealed class AddQuickScreenEffect {
    data class NavigateBackWithResult(val newPairId: Long) : AddQuickScreenEffect()

    data class NavigateSearchSet(val index: Int, val prohibitedCodes: List<CurrencyCode>) :
        AddQuickScreenEffect()

    data class NavigateSearchAdd(val prohibitedCodes: List<CurrencyCode>) :
        AddQuickScreenEffect()
}

enum class SearchNavResultType {
    ADD,
    SET,
}

class AddQuickViewModel(
    private val quickPairId: Long?,
    private val newCode: CurrencyCode?,
    private val reuseNotEdit: Boolean,
    private val groupId: Long?,
    private val quickRepo: QuickRepo,
    private val groupRepo: GroupRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val getGroupByIdOrCreateDefaultUseCase: GetGroupByIdOrCreateDefaultUseCase,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val analyticsManager: AnalyticsManager,
) : ViewModel(), ContainerHost<AddQuickScreenState, AddQuickScreenEffect> {
    override val container: Container<AddQuickScreenState, AddQuickScreenEffect> =
        container(AddQuickScreenState())

    init {
        analyticsManager.trackScreen("AddQuickScreen")

        intent {
            val groups = groupRepo.getAllSorted(GroupFeatureType.Quick)
            val quickPair = quickPairId?.let { quickRepo.getById(it) }
            quickPair?.let {
                val currencies =
                    listOf(
                        AmountStr(
                            quickPair.from,
                            quickPair.amount.toPlainString(),
                        ),
                    ) + quickPair.to.map { AmountStr(it.code, "") }
                val calc = calcToResult(currencies)

                reduce {
                    state.copy(
                        quickPairId = quickPairId,
                        currencies = calc,
                        group = quickPair.group,
                        availableGroups = groups,
                        initialized = true,
                    )
                }
                checkFinishEnabled()
            } ?: let {
                val group = getGroupByIdOrCreateDefaultUseCase(groupId, GroupFeatureType.Quick)
                val currencies =
                    newCode?.let {
                        listOf(AmountStr(newCode, ""))
                    } ?: state.currencies
                reduce {
                    state.copy(
                        currencies = currencies,
                        availableGroups = groups,
                        group = group,
                        initialized = true,
                    )
                }
            }
        }
    }

    fun onNavResult(result: SearchNavResult) {
        val type = SearchNavResultType.valueOf(result.key!!)
        when (type) {
            SearchNavResultType.ADD -> handleNavResAddCode(result.code)
            SearchNavResultType.SET -> handleNavResSetCode(result.pos!!, result.code)
        }
    }

    private fun handleNavResAddCode(code: CurrencyCode) =
        intent {
            val newAmounts = state.currencies + AmountStr(code, "")
            val calc = calcToResult(newAmounts)
            reduce { state.copy(currencies = calc) }
            checkFinishEnabled()
        }

    private fun handleNavResSetCode(
        index: Int,
        code: CurrencyCode,
    ) = intent {
        val mutable = state.currencies.toMutableList()
        val new = mutable[index].copy(code = code)
        mutable[index] = new
        val calc = calcToResult(mutable)
        reduce { state.copy(currencies = calc) }
    }

    fun onCurrencyRemove(removeIndex: Int) =
        intent {
            reduce {
                state.copy(
                    currencies =
                        state.currencies
                            .filterIndexed { index, _ -> index != removeIndex },
                )
            }
            checkFinishEnabled()
        }

    fun onGroupSelect(group: Group) =
        intent {
            reduce { state.copy(group = group) }
        }

    fun onGroupCreate(name: String) =
        intent {
            val group = groupRepo.new(name, GroupFeatureType.Quick)
            reduce {
                state.copy(group = group)
            }
        }

    fun onAssetAmountChange(input: String) =
        blockingIntent {
            val from = state.currencies.first()
            val new = from.copy(value = CurrUtils.validateInput(from.value, input))
            val calc = calcToResult(listOf(new) + state.currencies.drop(1))
            reduce { state.copy(currencies = calc) }
            checkFinishEnabled()
        }

    fun onSwapClick() =
        intent {
            if (state.currencies.size < 2)
                return@intent

            val newFrom = state.currencies.last()
            val newCurrencies =
                state.currencies.toMutableList().apply {
                    removeAt(lastIndex)
                    add(0, newFrom)
                }

            reduce {
                state.copy(currencies = newCurrencies)
            }
        }

    fun onPairsSwap(
        from: Int,
        to: Int,
    ) = intent {
        val new =
            state.currencies.toMutableList().apply {
                add(to, removeAt(from))
            }
        reduce {
            state.copy(currencies = new)
        }
    }

    fun onAddQuickPair() =
        intent {
            val from = state.currencies.first()
            val id =
                if (quickPairId != null) {
                    if (reuseNotEdit) 0 else quickPairId
                } else {
                    0
                }

            val quick =
                QuickPair(
                    id = id,
                    from = from.code,
                    amount = from.value.toBigDecimalArk(),
                    to = state.currencies.drop(1).map { it.toAmount() },
                    calculatedDate = OffsetDateTime.now(),
                    pinnedDate = null,
                    group = state.group,
                )
            val newId = quickRepo.insert(quick)
            codeUseStatRepo.codesUsed(
                quick.from,
                *quick.to.map { it.code }.toTypedArray(),
            )
            postSideEffect(AddQuickScreenEffect.NavigateBackWithResult(newId))
        }

    private suspend fun calcToResult(old: List<AmountStr>): List<AmountStr> {
        val from = old.first()
        val to = old.drop(1)
        val new =
            to.map {
                if (from.value == "") {
                    it.copy(value = "")
                } else {
                    val (amount, _) = convertUseCase.invoke(from.toAmount(), it.code)
                    val roundValue = CurrUtils.roundOff(amount.value)
                    AmountStr(it.code, roundValue)
                }
            }
        return listOf(from) + new
    }

    private fun checkFinishEnabled() =
        intent {
            val from = state.currencies.first()
            val to = state.currencies.drop(1)

            var finishEnabled = true

            if (from.value.toDoubleArk() == 0.0)
                finishEnabled = false

            if (to.isEmpty())
                finishEnabled = false

            reduce {
                state.copy(finishEnabled = finishEnabled)
            }
        }

    fun onSetCode(index: Int) =
        intent {
            val prohibitedCodes =
                state.currencies.map { it.code }.toMutableList().apply {
                    removeAt(index)
                }
            postSideEffect(AddQuickScreenEffect.NavigateSearchSet(index, prohibitedCodes))
        }

    fun onAddCode() =
        intent {
            val prohibitedCodes = state.currencies.map { it.code }
            postSideEffect(AddQuickScreenEffect.NavigateSearchAdd(prohibitedCodes))
        }
}

class AddQuickViewModelFactory @AssistedInject constructor(
    @Assisted("pairId") private val quickPairId: Long?,
    @Assisted("newCode") private val newCode: CurrencyCode?,
    @Assisted private val reuseNotEdit: Boolean,
    @Assisted("groupId") private val groupId: Long?,
    private val quickRepo: QuickRepo,
    private val groupRepo: GroupRepo,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val getGroupByIdOrCreateDefaultUseCase: GetGroupByIdOrCreateDefaultUseCase,
    private val convertUseCase: ConvertWithRateUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddQuickViewModel(
            quickPairId,
            newCode,
            reuseNotEdit,
            groupId,
            quickRepo,
            groupRepo,
            convertUseCase,
            getGroupByIdOrCreateDefaultUseCase,
            codeUseStatRepo,
            analyticsManager,
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("pairId") quickPairId: Long?,
            @Assisted("newCode") newCode: CurrencyCode?,
            reuseNotEdit: Boolean,
            @Assisted("groupId") group: Long?,
        ): AddQuickViewModelFactory
    }
}
