package dev.arkbuilders.rate.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.domain.usecase.GetTopResultUseCase
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import dev.arkbuilders.rate.presentation.shared.AppSharedFlowKey
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

data class SearchScreenState(
    val frequent: List<CurrencyName> = emptyList(),
    val all: List<CurrencyName> = emptyList(),
    val filter: String = "",
    val topResults: List<CurrencyName> = emptyList(),
    val topResultsFiltered: List<CurrencyName> = emptyList(),
    val initialized: Boolean = false
)

sealed class SearchScreenEffect {
    data object NavigateBack : SearchScreenEffect()
}

class SearchViewModel(
    private val appSharedFlowKeyString: String,
    private val pos: Int?,
    private val currencyRepo: CurrencyRepo,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val getTopResultUseCase: GetTopResultUseCase,
    private val analyticsManager: AnalyticsManager
) : ContainerHost<SearchScreenState, SearchScreenEffect>,
    ViewModel() {
    override val container: Container<SearchScreenState, SearchScreenEffect> =
        container(SearchScreenState())

    init {
        analyticsManager.trackScreen("SearchScreen")

        intent {
            val all = currencyRepo.getCurrencyNameUnsafe()
            val frequent = calcFrequentCurrUseCase.invoke()
                .map { currencyRepo.nameByCodeUnsafe(it) }
            val topResults = getTopResultUseCase()

            reduce {
                state.copy(
                    frequent = frequent,
                    all = all,
                    topResults = topResults,
                    initialized = true
                )
            }
        }
    }

    fun onInputChange(input: String) = blockingIntent {
        val filtered = state.topResults
            .filter {
                it.name.contains(input, ignoreCase = true) ||
                    it.code.contains(input, ignoreCase = true)
            }
        reduce { state.copy(filter = input, topResultsFiltered = filtered) }
    }

    fun onClick(name: CurrencyName) = intent {
        emitResult(name)
        postSideEffect(SearchScreenEffect.NavigateBack)
    }

    private suspend fun emitResult(name: CurrencyName) {
        val appFlowKey = AppSharedFlowKey.valueOf(appSharedFlowKeyString)
        when (appFlowKey) {
            AppSharedFlowKey.SetAssetCode ->
                AppSharedFlow.SetAssetCode.flow.emit(pos!! to name.code)

            AppSharedFlowKey.AddAsset -> AppSharedFlow.AddAsset.flow.emit(name.code)

            AppSharedFlowKey.AddPairAlertBase ->
                AppSharedFlow.AddPairAlertBase.flow.emit(name.code)

            AppSharedFlowKey.AddPairAlertTarget ->
                AppSharedFlow.AddPairAlertTarget.flow.emit(name.code)

            AppSharedFlowKey.SetQuickCode ->
                AppSharedFlow.SetQuickCode.flow.emit(pos!! to name.code)

            AppSharedFlowKey.PickBaseCurrency ->
                AppSharedFlow.PickBaseCurrency.flow.emit(name.code)

            AppSharedFlowKey.AddQuickCode ->
                AppSharedFlow.AddQuickCode.flow.emit(name.code)
        }
    }
}

class SearchViewModelFactory @AssistedInject constructor(
    @Assisted private val appSharedFlowKeyString: String,
    @Assisted private val pos: Int?,
    private val currencyRepo: CurrencyRepo,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val getTopResultUseCase: GetTopResultUseCase,
    private val analyticsManager: AnalyticsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(
            appSharedFlowKeyString,
            pos,
            currencyRepo,
            calcFrequentCurrUseCase,
            getTopResultUseCase,
            analyticsManager
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            appSharedFlowKeyString: String,
            pos: Int?
        ): SearchViewModelFactory
    }
}
