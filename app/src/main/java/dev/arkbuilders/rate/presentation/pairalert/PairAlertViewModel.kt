package dev.arkbuilders.rate.presentation.pairalert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.domain.model.PairAlert
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PairAlertRepo
import dev.arkbuilders.rate.domain.usecase.HandlePairAlertCheckUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
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

}

class PairAlertViewModel(
    private val pairAlertRepo: PairAlertRepo,
    private val currencyRepo: CurrencyRepo,
) : ViewModel(), ContainerHost<PairAlertScreenState, PairAlertEffect> {

    override val container: Container<PairAlertScreenState, PairAlertEffect> =
        container(
            PairAlertScreenState()
        )

    init {
        intent {
            if (isRatesAvailable().not())
                return@intent

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
        Timber.d("Remove pair alert ${pairAlert.id}")
        pairAlertRepo.delete(pairAlert.id)
    }

    private suspend fun isRatesAvailable() = currencyRepo.getCurrencyRate().isRight()
}

@Singleton
class PairAlertViewModelFactory @Inject constructor(
    private val pairAlertRepo: PairAlertRepo,
    private val currencyRepo: CurrencyRepo,
    private val handlePairAlertCheckUseCase: HandlePairAlertCheckUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PairAlertViewModel(
            pairAlertRepo,
            currencyRepo,
        ) as T
    }
}