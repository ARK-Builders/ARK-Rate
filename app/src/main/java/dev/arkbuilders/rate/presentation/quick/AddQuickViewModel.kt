package dev.arkbuilders.rate.presentation.quick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.data.toDoubleSafe
import dev.arkbuilders.rate.domain.model.AmountStr
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.domain.model.toDAmount
import dev.arkbuilders.rate.domain.model.toStrAmount
import dev.arkbuilders.rate.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.presentation.search.SearchViewModelFactory
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
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

data class AddQuickScreenState(
    val quickPairId: Long? = null,
    val currencies: List<AmountStr> = listOf(AmountStr("USD", "")),
    val group: String? = null,
    val availableGroups: List<String> = emptyList(),
    val finishEnabled: Boolean = false
)

sealed class AddQuickScreenEffect {
    data class NotifyPairAdded(val pair: QuickPair) : AddQuickScreenEffect()
    data object NavigateBack : AddQuickScreenEffect()
}

class AddQuickViewModel(
    private val quickPairId: Long?,
    private val newCode: CurrencyCode?,
    private val reuseNotEdit: Boolean,
    private val quickRepo: QuickRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val analyticsManager: AnalyticsManager
) : ViewModel(), ContainerHost<AddQuickScreenState, AddQuickScreenEffect> {

    override val container: Container<AddQuickScreenState, AddQuickScreenEffect> =
        container(AddQuickScreenState())

    init {
        analyticsManager.trackScreen("AddQuickScreen")

        AppSharedFlow.SetQuickCode.flow.onEach { (index, code) ->
            intent {
                val mutable = state.currencies.toMutableList()
                val new = mutable[index].copy(code = code)
                mutable[index] = new
                val calc = calcToResult(mutable)
                reduce { state.copy(currencies = calc) }
            }
        }.launchIn(viewModelScope)

        AppSharedFlow.AddQuickCode.flow.onEach { code ->
            intent {
                val newAmounts = state.currencies + AmountStr(code, "")
                val calc = calcToResult(newAmounts)
                reduce {  state.copy(currencies = calc) }
                checkFinishEnabled()
            }
        }.launchIn(viewModelScope)

        intent {
            val groups =
                quickRepo.getAll().mapNotNull { it.group }.distinct()
            val quickPair = quickPairId?.let { quickRepo.getById(it) }
            quickPair?.let {
                val currencies = listOf(
                    AmountStr(
                        quickPair.from,
                        quickPair.amount.toString()
                    )
                ) + quickPair.to.map { AmountStr(it.code, "") }
                val calc = calcToResult(currencies)

                reduce {
                    state.copy(
                        quickPairId = quickPairId,
                        currencies = calc,
                        group = quickPair.group,
                        availableGroups = groups
                    )
                }
                checkFinishEnabled()
            } ?: reduce {
                val currencies = newCode?.let {
                    listOf(AmountStr(newCode, ""))
                } ?: state.currencies
                state.copy(currencies = currencies, availableGroups = groups)
            }
        }
    }

    fun onCurrencyRemove(removeIndex: Int) = intent {
        reduce {
            state.copy(
                currencies = state.currencies
                    .filterIndexed { index, _ -> index != removeIndex }
            )
        }
        checkFinishEnabled()
    }

    fun onGroupSelect(group: String?) = intent {
        reduce { state.copy(group = group) }
    }

    fun onAssetAmountChange(input: String) = blockingIntent {
        val from = state.currencies.first()
        val new = from.copy(value = CurrUtils.validateInput(from.value, input))
        val calc = calcToResult(listOf(new) + state.currencies.drop(1))
        reduce { state.copy(currencies = calc) }
        checkFinishEnabled()
    }

    fun onAddQuickPair() = intent {
        val from = state.currencies.first()
        val id = if (quickPairId != null) {
            if (reuseNotEdit) 0 else quickPairId
        } else 0

        val quick = QuickPair(
            id = id,
            from = from.code,
            amount = from.value.toDouble(),
            to = state.currencies.drop(1).map { it.toDAmount() },
            calculatedDate = OffsetDateTime.now(),
            pinnedDate = null,
            group = state.group,
        )
        quickRepo.insert(quick)
        codeUseStatRepo.codesUsed(
            quick.from, *quick.to.map { it.code }.toTypedArray()
        )
        postSideEffect(AddQuickScreenEffect.NotifyPairAdded(quick))
        postSideEffect(AddQuickScreenEffect.NavigateBack)
    }

    private suspend fun calcToResult(old: List<AmountStr>): List<AmountStr> {
        val from = old.first()
        val to = old.drop(1)
        val new = to.map {
            if (from.value == "") {
                it.copy(value = "")
            } else {
                val (amount, _) = convertUseCase.invoke(from.toDAmount(), it.code)
                amount.toStrAmount()
            }
        }
        return listOf(from) + new
    }

    private fun checkFinishEnabled() = intent {
        val from = state.currencies.first()
        val to = state.currencies.drop(1)

        var finishEnabled = true

        if (from.value.toDoubleSafe() == 0.0)
            finishEnabled = false

        if (to.isEmpty())
            finishEnabled = false

        reduce {
            state.copy(finishEnabled = finishEnabled)
        }
    }

}

class AddQuickViewModelFactory @AssistedInject constructor(
    @Assisted private val quickPairId: Long?,
    @Assisted private val newCode: CurrencyCode?,
    @Assisted private val reuseNotEdit: Boolean,
    private val quickRepo: QuickRepo,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val analyticsManager: AnalyticsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddQuickViewModel(
            quickPairId,
            newCode,
            reuseNotEdit,
            quickRepo,
            convertUseCase,
            codeUseStatRepo,
            analyticsManager
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            quickPairId: Long?,
            newCode: CurrencyCode?,
            reuseNotEdit: Boolean,
        ): AddQuickViewModelFactory
    }
}