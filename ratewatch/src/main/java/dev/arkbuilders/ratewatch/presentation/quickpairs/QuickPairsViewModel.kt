package dev.arkbuilders.ratewatch.presentation.quickpairs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.ratewatch.domain.model.PinnedQuickPair
import dev.arkbuilders.ratewatch.domain.model.QuickPair
import dev.arkbuilders.ratewatch.domain.model.QuickScreenPage
import dev.arkbuilders.ratewatch.domain.model.TimestampType
import dev.arkbuilders.ratewatch.domain.repo.CurrencyRepo
import dev.arkbuilders.ratewatch.domain.repo.QuickRepo
import dev.arkbuilders.ratewatch.domain.repo.TimestampRepo
import dev.arkbuilders.ratewatch.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.ratewatch.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.ratewatch.domain.usecase.GetTopResultUseCase
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

class QuickPairsViewModel @Inject constructor(
    private val currencyRepo: CurrencyRepo,
    private val quickRepo: QuickRepo,
    private val timestampRepo: TimestampRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val getTopResultUseCase: GetTopResultUseCase,
) : ViewModel() {

    init {
        initQuickPairs()
    }

    private fun initQuickPairs() {

        quickRepo.allFlow().drop(1).onEach { quick ->
            val pages = mapPairsToPages(quick)
        }.launchIn(viewModelScope)
        viewModelScope.launch {
            val allCurrencies = currencyRepo.getCurrencyNameUnsafe()
            calcFrequentCurrUseCase.flow().drop(1).onEach {
                val frequent =
                    calcFrequentCurrUseCase.invoke()
                        .map { currencyRepo.nameByCodeUnsafe(it) }
                val topResults = getTopResultUseCase()

            }.launchIn(viewModelScope)

            val frequent =
                calcFrequentCurrUseCase()
                    .map { currencyRepo.nameByCodeUnsafe(it) }
            val topResults = getTopResultUseCase()
            val pages = mapPairsToPages(quickRepo.getAll())
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

    class QuickPairsViewModelFactory @AssistedInject constructor(
        private val quickRepo: QuickRepo,
        private val currencyRepo: CurrencyRepo,
        private val timestampRepo: TimestampRepo,
        private val convertUseCase: ConvertWithRateUseCase,
        private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
        private val getTopResultUseCase: GetTopResultUseCase,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return QuickPairsViewModel(
                currencyRepo,
                quickRepo,
                timestampRepo,
                convertUseCase,
                calcFrequentCurrUseCase,
                getTopResultUseCase,
            ) as T
        }

        @AssistedFactory
        interface Factory {
            fun create(): QuickPairsViewModelFactory
        }
    }
}
