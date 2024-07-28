package dev.arkbuilders.rate.presentation.quick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.domain.model.TimestampType
import dev.arkbuilders.rate.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.domain.repo.Prefs
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.repo.TimestampRepo
import dev.arkbuilders.rate.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.domain.usecase.GetTopResultUseCase
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import dev.arkbuilders.rate.presentation.ui.NotifyAddedSnackbarVisuals
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
import java.time.OffsetDateTime

data class QuickScreenPage(
    val group: String?,
    val pinned: List<PinnedQuickPair>,
    val notPinned: List<QuickPair>
)

data class OptionsData(val pair: QuickPair)

data class QuickScreenState(
    val filter: String = "",
    val currencies: List<CurrencyName> = emptyList(),
    val frequent: List<CurrencyName> = emptyList(),
    val topResults: List<CurrencyName> = emptyList(),
    val pages: List<QuickScreenPage> = emptyList(),
    val optionsData: OptionsData? = null,
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
    private val quickRepo: QuickRepo,
    private val timestampRepo: TimestampRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val getTopResultUseCase: GetTopResultUseCase,
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

            quickRepo.allFlow().drop(1).onEach { quick ->
                intent {
                    val pages = mapPairsToPages(quick)
                    reduce {
                        state.copy(
                            pages = pages
                        )
                    }
                }
            }.launchIn(viewModelScope)

            val allCurrencies = currencyRepo.getCurrencyNameUnsafe()
            calcFrequentCurrUseCase.flow().drop(1).onEach {
                val frequent = calcFrequentCurrUseCase.invoke()
                    .map { currencyRepo.nameByCodeUnsafe(it) }
                val topResults = getTopResultUseCase()
                reduce {
                    state.copy(
                        frequent = frequent,
                        topResults = topResults
                    )
                }
            }.launchIn(viewModelScope)

            val frequent = calcFrequentCurrUseCase()
                .map { currencyRepo.nameByCodeUnsafe(it) }
            val topResults = getTopResultUseCase()
            val pages = mapPairsToPages(quickRepo.getAll())
            reduce {
                state.copy(
                    currencies = allCurrencies,
                    frequent = frequent,
                    topResults = topResults,
                    pages = pages,
                    initialized = true
                )
            }
        }
    }

    fun onShowOptions(pair: QuickPair) = intent {
        reduce { state.copy(optionsData = OptionsData(pair)) }
    }

    fun onHideOptions() = intent {
        reduce { state.copy(optionsData = null) }
    }

    fun onPin(pair: QuickPair) = intent {
        val newPair = pair.copy(pinnedDate = OffsetDateTime.now())
        quickRepo.insert(newPair)
    }

    fun onUnpin(pair: QuickPair) = intent {
        val newPair = pair.copy(pinnedDate = null)
        quickRepo.insert(newPair)
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

    private suspend fun mapPairsToPages(pairs: List<QuickPair>): List<QuickScreenPage> {
        val refreshDate = timestampRepo.getTimestamp(TimestampType.FetchFiat)
        val pages = pairs
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
        return pages
    }


    private suspend fun mapPairToPinned(
        pair: QuickPair,
        refreshDate: OffsetDateTime
    ): PinnedQuickPair {
        val actualTo = pair.to.map { to ->
            val (amount, _) = convertUseCase.invoke(pair.from, pair.amount, to.code)
            amount
        }
        return PinnedQuickPair(pair, actualTo, refreshDate)
    }
}

class QuickViewModelFactory @AssistedInject constructor(
    private val quickRepo: QuickRepo,
    private val currencyRepo: CurrencyRepo,
    private val timestampRepo: TimestampRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val getTopResultUseCase: GetTopResultUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuickViewModel(
            currencyRepo,
            quickRepo,
            timestampRepo,
            convertUseCase,
            calcFrequentCurrUseCase,
            getTopResultUseCase,
            analyticsManager
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(): QuickViewModelFactory
    }
}