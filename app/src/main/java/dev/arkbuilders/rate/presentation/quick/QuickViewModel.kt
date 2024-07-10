package dev.arkbuilders.rate.presentation.quick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.domain.repo.Prefs
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import dev.arkbuilders.rate.presentation.ui.NotifyAddedSnackbarVisuals
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

data class QuickScreenPage(
    val group: String?,
    val pairs: List<QuickPair>
)

data class QuickScreenState(
    val filter: String = "",
    val currencies: List<CurrencyName> = emptyList(),
    val frequent: List<CurrencyName> = emptyList(),
    val topResults: List<CurrencyName> = emptyList(),
    val pages: List<QuickScreenPage> = emptyList(),
    val initialized: Boolean = false
)

sealed class QuickScreenEffect {
    data class ShowSnackbarAdded(
        val visuals: NotifyAddedSnackbarVisuals
    ) : QuickScreenEffect()

    data class ShowRemovedSnackbar(val pair: QuickPair) : QuickScreenEffect()
}

class QuickViewModel(
    private val currencyRepo: CurrencyRepo,
    private val assetsRepo: PortfolioRepo,
    private val quickRepo: QuickRepo,
    private val prefs: Prefs,
    private val convertUseCase: ConvertWithRateUseCase,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModel(), ContainerHost<QuickScreenState, QuickScreenEffect> {
    override val container: Container<QuickScreenState, QuickScreenEffect> =
        container(QuickScreenState())

    init {
        analyticsManager.trackScreen("QuickScreen")

        intent {
            if (isRatesAvailable().not())
                return@intent

            AppSharedFlow.ShowAddedSnackbarQuick.flow.onEach { visuals ->
                postSideEffect(QuickScreenEffect.ShowSnackbarAdded(visuals))
            }.launchIn(viewModelScope)

            quickRepo.allFlow().onEach { all ->
                val pages = all.reversed().groupBy { it.group }
                    .map { (group, pairs) -> QuickScreenPage(group, pairs) }
                intent {
                    reduce {
                        state.copy(
                            pages = pages,
                            initialized = true
                        )
                    }
                }
            }.launchIn(viewModelScope)

            val all = currencyRepo.getCurrencyName().getOrNull()!!
            val frequent = calcFrequentCurrUseCase.invoke()
                .map { currencyRepo.nameByCodeUnsafe(it) }
            val topResults = frequent + (all - frequent)

            reduce {
                state.copy(
                    currencies = all,
                    frequent = frequent,
                    topResults = topResults
                )
            }
        }
    }

    fun onFilterChanged(filter: String) = blockingIntent {
        reduce { state.copy(filter = filter) }
    }

    fun onDelete(pair: QuickPair) = intent {
        val deleted = quickRepo.delete(pair.id)
        if (deleted) {
            postSideEffect(QuickScreenEffect.ShowRemovedSnackbar(pair))
        }
    }

    fun undoDelete(pair: QuickPair) = intent {
        quickRepo.insert(pair)
    }

    private suspend fun isRatesAvailable() = currencyRepo.getCurrencyRate().isRight()
}

class QuickViewModelFactory @AssistedInject constructor(
    private val assetsRepo: PortfolioRepo,
    private val quickRepo: QuickRepo,
    private val currencyRepo: CurrencyRepo,
    private val prefs: Prefs,
    private val convertUseCase: ConvertWithRateUseCase,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuickViewModel(
            currencyRepo,
            assetsRepo,
            quickRepo,
            prefs,
            convertUseCase,
            calcFrequentCurrUseCase,
            analyticsManager
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(): QuickViewModelFactory
    }
}