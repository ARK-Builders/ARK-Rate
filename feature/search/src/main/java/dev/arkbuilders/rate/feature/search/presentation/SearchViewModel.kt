package dev.arkbuilders.rate.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyName
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.core.domain.usecase.GetTopResultUseCase
import dev.arkbuilders.rate.core.presentation.AppSharedFlow
import dev.arkbuilders.rate.core.presentation.AppSharedFlowKey
import dev.arkbuilders.rate.feature.search.di.SearchScope
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

data class CurrencySearchModel(
    val code: CurrencyCode,
    val name: String,
    val isProhibited: Boolean,
)

data class SearchScreenState(
    val frequent: List<CurrencySearchModel> = emptyList(),
    val all: List<CurrencySearchModel> = emptyList(),
    val filter: String = "",
    val topResults: List<CurrencySearchModel> = emptyList(),
    val topResultsFiltered: List<CurrencySearchModel> = emptyList(),
    val initialized: Boolean = false,
    val showCodeProhibitedDialog: Boolean = false,
)

sealed class SearchScreenEffect {
    data object NavigateBack : SearchScreenEffect()
}

class SearchViewModel(
    private val appSharedFlowKeyString: String,
    private val pos: Int?,
    private val prohibitedCodes: List<CurrencyCode>?,
    private val currencyRepo: CurrencyRepo,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val getTopResultUseCase: GetTopResultUseCase,
    private val analyticsManager: AnalyticsManager,
) : ContainerHost<SearchScreenState, SearchScreenEffect>,
    ViewModel() {
    override val container: Container<SearchScreenState, SearchScreenEffect> =
        container(SearchScreenState())

    init {
        analyticsManager.trackScreen("SearchScreen")

        intent {
            val all = currencyRepo.getCurrencyName().mapToSearchModel()
            val frequent =
                calcFrequentCurrUseCase.invoke()
                    .map { currencyRepo.nameByCode(it) }
                    .mapToSearchModel()
            val topResults = getTopResultUseCase().mapToSearchModel()

            reduce {
                state.copy(
                    frequent = frequent,
                    all = all,
                    topResults = topResults,
                    initialized = true,
                )
            }
        }
    }

    fun onInputChange(input: String) =
        blockingIntent {
            val filtered =
                state.topResults
                    .filter {
                        it.name.contains(input, ignoreCase = true) ||
                            it.code.contains(input, ignoreCase = true)
                    }
            reduce { state.copy(filter = input, topResultsFiltered = filtered) }
        }

    fun onClick(model: CurrencySearchModel) =
        intent {
            prohibitedCodes?.let {
                if (model.code in prohibitedCodes) {
                    reduce {
                        state.copy(showCodeProhibitedDialog = true)
                    }
                    return@intent
                }
            }

            emitResult(CurrencyName(code = model.code, name = model.name))
            postSideEffect(SearchScreenEffect.NavigateBack)
        }

    fun onCodeProhibitedDialogDismiss() =
        intent {
            reduce { state.copy(showCodeProhibitedDialog = false) }
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

    private fun List<CurrencyName>.mapToSearchModel() =
        map { name ->
            val isProhibited = prohibitedCodes?.let { name.code in it } ?: false
            CurrencySearchModel(code = name.code, name = name.name, isProhibited = isProhibited)
        }
}

class SearchViewModelFactory @AssistedInject constructor(
    @Assisted private val appSharedFlowKeyString: String,
    @Assisted private val pos: Int?,
    @Assisted private val prohibitedCodes: List<CurrencyCode>?,
    private val currencyRepo: CurrencyRepo,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val getTopResultUseCase: GetTopResultUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(
            appSharedFlowKeyString,
            pos,
            prohibitedCodes,
            currencyRepo,
            calcFrequentCurrUseCase,
            getTopResultUseCase,
            analyticsManager,
        ) as T
    }

    @SearchScope
    @AssistedFactory
    interface Factory {
        fun create(
            appSharedFlowKeyString: String,
            pos: Int?,
            prohibitedCodes: List<CurrencyCode>?,
        ): SearchViewModelFactory
    }
}
