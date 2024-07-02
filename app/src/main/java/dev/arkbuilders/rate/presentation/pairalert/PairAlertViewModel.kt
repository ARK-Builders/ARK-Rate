package dev.arkbuilders.rate.presentation.pairalert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.domain.model.PairAlert
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PairAlertRepo
import dev.arkbuilders.rate.domain.usecase.HandlePairAlertCheckUseCase
import dev.arkbuilders.rate.presentation.portfolio.PortfolioScreenEffect
import dev.arkbuilders.rate.presentation.quick.QuickScreenEffect
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import dev.arkbuilders.rate.presentation.ui.NotifyAddedSnackbarVisuals
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

data class PairAlertScreenPage(
    val group: String?,
    val created: List<PairAlert>,
    val oneTimeTriggered: List<PairAlert>
)

data class PairAlertScreenState(
    val pages: List<PairAlertScreenPage> = emptyList(),
    val initialized: Boolean = false
)

sealed class PairAlertEffect {
    data class ShowSnackbarAdded(
        val visuals: NotifyAddedSnackbarVisuals
    ): PairAlertEffect()
    data class ShowRemovedSnackbar(val pair: PairAlert): PairAlertEffect()
}

class PairAlertViewModel(
    private val pairAlertRepo: PairAlertRepo,
    private val currencyRepo: CurrencyRepo,
    private val analyticsManager: AnalyticsManager,
) : ViewModel(), ContainerHost<PairAlertScreenState, PairAlertEffect> {

    override val container: Container<PairAlertScreenState, PairAlertEffect> =
        container(
            PairAlertScreenState()
        )

    init {
        analyticsManager.trackScreen("PairAlertScreen")

        intent {
            if (isRatesAvailable().not())
                return@intent

            AppSharedFlow.ShowAddedSnackbarQuick.flow.onEach { visuals ->
                postSideEffect(PairAlertEffect.ShowSnackbarAdded(visuals))
            }.launchIn(viewModelScope)

            pairAlertRepo.getAllFlow().onEach { all ->
                val pages = all.reversed().groupBy { it.group }
                    .map { (group, pairAlertList) ->
                        val oneTimeTriggered =
                            pairAlertList.filter { it.triggered() && it.oneTimeNotRecurrent && !it.enabled }
                        val created = pairAlertList - oneTimeTriggered

                        PairAlertScreenPage(group, created, oneTimeTriggered)
                    }
                intent {
                    reduce {
                        state.copy(pages = pages, initialized = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun onEnableToggle(pairAlert: PairAlert, enabled: Boolean) = intent {
        val newPairAlert = pairAlert.copy(enabled = enabled)
        pairAlertRepo.insert(newPairAlert)
    }

    fun onDelete(pairAlert: PairAlert) = intent {
        val deleted = pairAlertRepo.delete(pairAlert.id)
        if (deleted)
            postSideEffect(PairAlertEffect.ShowRemovedSnackbar(pairAlert))
    }

    fun undoDelete(pair: PairAlert) = intent {
        pairAlertRepo.insert(pair)
    }

    private suspend fun isRatesAvailable() = currencyRepo.getCurrencyRate().isRight()
}

@Singleton
class PairAlertViewModelFactory @Inject constructor(
    private val pairAlertRepo: PairAlertRepo,
    private val currencyRepo: CurrencyRepo,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PairAlertViewModel(
            pairAlertRepo,
            currencyRepo,
            analyticsManager
        ) as T
    }
}