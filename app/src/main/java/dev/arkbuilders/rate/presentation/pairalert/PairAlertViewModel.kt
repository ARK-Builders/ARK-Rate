package dev.arkbuilders.rate.presentation.pairalert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import arrow.core.split
import dev.arkbuilders.rate.data.db.PairAlertRepo
import dev.arkbuilders.rate.data.model.PairAlert
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.forEach
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
    val filter: String = "",
    val pages: List<PairAlertScreenPage> = emptyList()
)

sealed class PairAlertEffect {

}

class PairAlertViewModel(
    private val pairAlertRepo: PairAlertRepo
) : ViewModel(), ContainerHost<PairAlertScreenState, PairAlertEffect> {

    override val container: Container<PairAlertScreenState, PairAlertEffect> =
        container(
            PairAlertScreenState()
        )

    init {
        viewModelScope.launch {
            pairAlertRepo.getAllFlow().collectLatest { all ->
                val pages = all.groupBy { it.group }.map { (group, pairAlertList) ->
                    val oneTimeTriggered = pairAlertList.filter { it.triggered && it.oneTimeNotRecurrent  }
                    val created = pairAlertList - oneTimeTriggered

                    PairAlertScreenPage(group, created, oneTimeTriggered)
                }
                intent {
                    reduce {
                        state.copy(pages = pages)
                    }
                }
            }
        }
    }

    fun onDelete(pairAlert: PairAlert) = intent {
        Timber.d("Remove pair alert ${pairAlert.id}")
        pairAlertRepo.delete(pairAlert.id)
    }
}

@Singleton
class PairAlertViewModelFactory @Inject constructor(
    private val pairAlertRepo: PairAlertRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PairAlertViewModel(pairAlertRepo) as T
    }
}