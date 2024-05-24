package dev.arkbuilders.rate.presentation.pairalert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.db.PairAlertRepo
import dev.arkbuilders.rate.data.model.CurrencyAmount
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.PairAlert
import dev.arkbuilders.rate.data.toDoubleSafe
import dev.arkbuilders.rate.presentation.addcurrency.AddCurrencySideEffect
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
import javax.inject.Inject
import javax.inject.Singleton

data class AddPairAlertScreenState(
    val targetCode: CurrencyCode = "BTC",
    val baseCode: CurrencyCode  = "USD",
    val priceOrPercent: Either<String, String> = Either.Left(""),
    val currentPrice: Double = 0.0,
    val aboveNotBelow: Boolean = true,
    val group: String? = null,
    val oneTimeNotRecurrent: Boolean = true,
    val availableGroups: List<String> = emptyList()
)


sealed class AddPairAlertScreenEffect {
    data object NavigateBack : AddPairAlertScreenEffect()
    class NotifyPairAdded(val pair: PairAlert) : AddPairAlertScreenEffect()
}

class AddPairAlertViewModel(
    private val currencyRepo: GeneralCurrencyRepo,
    private val pairAlertRepo: PairAlertRepo
) : ViewModel(), ContainerHost<AddPairAlertScreenState, AddPairAlertScreenEffect> {
    override val container: Container<AddPairAlertScreenState, AddPairAlertScreenEffect> =
        container(AddPairAlertScreenState())

    init {
        AppSharedFlow.AddPairAlertTarget.flow.onEach {
            initOnCodeChange(newTarget = it)
        }.launchIn(viewModelScope)

        AppSharedFlow.AddPairAlertBase.flow.onEach {
            initOnCodeChange(newBase = it)
        }.launchIn(viewModelScope)

        initOnCodeChange()

        intent {
            val groups =
                pairAlertRepo.getAll().mapNotNull { it.group }.distinct()
            reduce {
                state.copy(availableGroups = groups)
            }
        }
    }

    private fun initOnCodeChange(
        newTarget: CurrencyCode? = null,
        newBase: CurrencyCode? = null
    ) {
        intent {
            val target = newTarget ?: state.targetCode
            val base = newBase ?: state.baseCode
            val baseRate =
                currencyRepo.getCurrencyRate().find { it.code == base }
            val targetRate =
                currencyRepo.getCurrencyRate().find { it.code == target }

            val currentPrice =
                CurrUtils.roundOff(targetRate!!.rate / baseRate!!.rate)

            reduce {
                state.copy(
                    currentPrice = currentPrice,
                    priceOrPercent = Either.Left(
                        CurrUtils.roundOff(currentPrice * 1.1).toString()
                    ),
                    targetCode = target,
                    baseCode = base
                )
            }
        }
    }

    fun onPriceOrPercentInputChanged(input: String) = blockingIntent {
        state.priceOrPercent.fold(
            ifLeft = { handlePriceChanged(input) },
            ifRight = { handlePercentChanged(input) }
        )
    }

    fun onPriceOrPercentChanged(priceNotPercent: Boolean) = intent {
        reduce {
            val priceOrPercent = if (priceNotPercent)
                Either.Left(CurrUtils.roundOff(state.currentPrice * 1.1).toString())
            else
                Either.Right("5")

            state.copy(priceOrPercent = priceOrPercent)
        }
    }

    fun onOneTimeChanged(oneTimeNotRecurrent: Boolean) = intent {
        reduce { state.copy(oneTimeNotRecurrent = oneTimeNotRecurrent) }
    }

    fun onSaveClick() = intent {
        val (price, percent) = state.priceOrPercent.fold(
            ifLeft = { price -> price.toDouble() to null },
            ifRight = { percent -> (state.currentPrice * (1.0 + percent.toDouble()/100)) to percent.toDouble() },
        )

        val pairAlert = PairAlert(
            id = 0,
            targetCode = state.targetCode,
            baseCode = state.baseCode,
            targetPrice = price,
            startPrice = state.currentPrice,
            alertPercent = percent,
            oneTimeNotRecurrent = true,
            enabled = true,
            priceNotPercent = true,
            lastDateTriggered = null,
            triggered = false,
            group = state.group
        )
        pairAlertRepo.insert(pairAlert)
        postSideEffect(AddPairAlertScreenEffect.NotifyPairAdded(pairAlert))
        postSideEffect(AddPairAlertScreenEffect.NavigateBack)
    }

    fun onGroupSelect(group: String?) = intent {
        reduce { state.copy(group = group) }
    }

    private fun handlePriceChanged(input: String) = blockingIntent {
        val newPrice = state.priceOrPercent
            .mapLeft { oldPrice ->
                CurrUtils.validateInput(
                    oldPrice,
                    input
                )
            }
        reduce {
            state.copy(
                priceOrPercent = newPrice,
                aboveNotBelow = newPrice.leftOrNull()!!.toDoubleSafe() > state.currentPrice
            )
        }
    }

    private fun handlePercentChanged(input: String) = blockingIntent {
        val newPercent = state.priceOrPercent
            .map { oldPercent ->
                CurrUtils.validateInputWithMinusChar(
                    oldPercent,
                    input
                )
            }
        reduce {
            state.copy(
                priceOrPercent = newPercent,
                aboveNotBelow = newPercent.getOrNull()!!.toDoubleSafe() > 0
            )
        }
    }
}

@Singleton
class AddPairAlertViewModelFactory @Inject constructor(
    private val currencyRepo: GeneralCurrencyRepo,
    private val pairAlertRepo: PairAlertRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddPairAlertViewModel(
            currencyRepo,
            pairAlertRepo
        ) as T
    }
}