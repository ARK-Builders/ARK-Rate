package dev.arkbuilders.rate.feature.pairalert.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.divideArk
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.toBigDecimalArk
import dev.arkbuilders.rate.core.domain.toDoubleArk
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.presentation.AppSharedFlow
import dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert
import dev.arkbuilders.rate.feature.pairalert.domain.repo.PairAlertRepo
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.math.BigDecimal

data class AddPairAlertScreenState(
    val targetCode: CurrencyCode = "BTC",
    val baseCode: CurrencyCode = "USD",
    val priceOrPercent: Either<String, String> = Either.Left(""),
    val currentPrice: BigDecimal = BigDecimal.ZERO,
    val aboveNotBelow: Boolean = true,
    val group: String? = null,
    val oneTimeNotRecurrent: Boolean = true,
    val availableGroups: List<String> = emptyList(),
    val finishEnabled: Boolean = true,
    val editExisting: Boolean = false,
)

sealed class AddPairAlertScreenEffect {
    data object NavigateBack : AddPairAlertScreenEffect()

    class NotifyPairAdded(val pair: PairAlert) : AddPairAlertScreenEffect()

    data class NavigateSearchTarget(
        val prohibitedCodes: List<CurrencyCode>,
    ) : AddPairAlertScreenEffect()

    data class NavigateSearchBase(
        val prohibitedCodes: List<CurrencyCode>,
    ) : AddPairAlertScreenEffect()
}

class AddPairAlertViewModel(
    private val pairAlertId: Long?,
    private val currencyRepo: CurrencyRepo,
    private val pairAlertRepo: PairAlertRepo,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModel(), ContainerHost<AddPairAlertScreenState, AddPairAlertScreenEffect> {
    override val container: Container<AddPairAlertScreenState, AddPairAlertScreenEffect> =
        container(AddPairAlertScreenState())

    init {
        analyticsManager.trackScreen("AddPairAlertScreen")

        AppSharedFlow.AddPairAlertTarget.flow.onEach {
            initOnCodeChange(newTarget = it)
        }.launchIn(viewModelScope)

        AppSharedFlow.AddPairAlertBase.flow.onEach {
            initOnCodeChange(newBase = it)
        }.launchIn(viewModelScope)

        pairAlertId?.let {
            setupFromExisting()
            intent {
                checkAboveNotBelow()
            }
        } ?: initOnCodeChange()

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
        newBase: CurrencyCode? = null,
    ) {
        intent {
            val target = newTarget ?: state.targetCode
            val base = newBase ?: state.baseCode
            val (_, currentPrice) =
                convertUseCase(
                    fromCode = target,
                    toCode = base,
                )
            val newState =
                state.copy(
                    currentPrice = currentPrice,
                    targetCode = target,
                    baseCode = base,
                )
            val priceOrPercent = calcNewPriceOrPercent(newState)

            reduce {
                newState.copy(priceOrPercent = priceOrPercent)
            }
            checkFinishEnabled()
        }
    }

    fun onPriceOrPercentInputChanged(input: String) =
        blockingIntent {
            state.priceOrPercent.fold(
                ifLeft = { handlePriceChanged(input) },
                ifRight = { handlePercentChanged(input) },
            )
            checkFinishEnabled()
        }

    fun onPriceOrPercentChanged(priceNotPercent: Boolean) =
        intent {
            reduce {
                val newState =
                    state.copy(
                        priceOrPercent =
                            if (priceNotPercent)
                                Either.Left("")
                            else
                                Either.Right(""),
                    )
                val newPriceOrPercent = calcNewPriceOrPercent(newState)
                newState.copy(priceOrPercent = newPriceOrPercent)
            }
            checkAboveNotBelow()
        }

    fun onIncreaseToggle() =
        intent {
            if (state.oneTimeNotRecurrent && state.priceOrPercent.isLeft())
                return@intent

            val newPriceOrPercent =
                state.priceOrPercent.mapLeft {
                    if (it.startsWith("-"))
                        it.replace("-", "")
                    else
                        "-$it"
                }.map {
                    if (it.startsWith("-"))
                        it.replace("-", "")
                    else
                        "-$it"
                }
            reduce { state.copy(priceOrPercent = newPriceOrPercent) }
            checkAboveNotBelow()
        }

    fun onOneTimeChanged(oneTimeNotRecurrent: Boolean) =
        intent {
            reduce {
                val newState = state.copy(oneTimeNotRecurrent = oneTimeNotRecurrent)
                val newPriceOrPercent = calcNewPriceOrPercent(newState)
                newState.copy(priceOrPercent = newPriceOrPercent)
            }
            checkAboveNotBelow()
        }

    fun onSaveClick() =
        intent {
            val targetPrice =
                state.priceOrPercent.fold(
                    ifLeft = { price ->
                        if (state.oneTimeNotRecurrent)
                            price.toBigDecimalArk()
                        else
                            (state.currentPrice + price.toBigDecimalArk())
                    },
                    ifRight = { percent ->
                        val percentFactor =
                            BigDecimal.ONE +
                                percent.toBigDecimalArk().divideArk(BigDecimal.valueOf(100))
                        state.currentPrice * percentFactor
                    },
                )

            val percent = state.priceOrPercent.getOrNull()?.toDoubleArk()

            val id = if (state.editExisting) pairAlertId!! else 0

            val pairAlert =
                PairAlert(
                    id = id,
                    targetCode = state.targetCode,
                    baseCode = state.baseCode,
                    targetPrice = targetPrice,
                    startPrice = state.currentPrice,
                    percent = percent,
                    oneTimeNotRecurrent = state.oneTimeNotRecurrent,
                    enabled = true,
                    lastDateTriggered = null,
                    group = state.group,
                )
            pairAlertRepo.insert(pairAlert)
            codeUseStatRepo.codesUsed(pairAlert.baseCode, pairAlert.targetCode)
            postSideEffect(AddPairAlertScreenEffect.NotifyPairAdded(pairAlert))
            postSideEffect(AddPairAlertScreenEffect.NavigateBack)
        }

    fun onGroupSelect(group: String?) =
        intent {
            reduce { state.copy(group = group) }
        }

    private fun handlePriceChanged(input: String) =
        blockingIntent {
            val newPrice =
                state.priceOrPercent
                    .mapLeft { oldPrice ->
                        if (state.oneTimeNotRecurrent) {
                            CurrUtils.validateInput(
                                oldPrice,
                                input,
                            )
                        } else {
                            CurrUtils.validateInputWithMinusChar(
                                oldPrice,
                                input,
                            )
                        }
                    }
            reduce {
                state.copy(
                    priceOrPercent = newPrice,
                )
            }
            checkAboveNotBelow()
        }

    private fun handlePercentChanged(input: String) =
        blockingIntent {
            val newPercent =
                state.priceOrPercent
                    .map { oldPercent ->
                        CurrUtils.validateInputWithMinusChar(
                            oldPercent,
                            input,
                        )
                    }
            reduce {
                state.copy(
                    priceOrPercent = newPercent,
                )
            }
            checkAboveNotBelow()
        }

    private fun checkAboveNotBelow() =
        intent {
            val aboveNotBelow =
                state.priceOrPercent.fold(
                    ifLeft = { price ->
                        if (state.oneTimeNotRecurrent) {
                            price.toBigDecimalArk() > state.currentPrice
                        } else {
                            price.toDoubleArk() > 0
                        }
                    },
                    ifRight = { percent ->
                        percent.toDoubleArk() > 0
                    },
                )
            reduce {
                state.copy(aboveNotBelow = aboveNotBelow)
            }
        }

    private fun calcNewPriceOrPercent(state: AddPairAlertScreenState): Either<String, String> {
        return state.priceOrPercent
            .mapLeft { price ->
                if (state.oneTimeNotRecurrent) {
                    CurrUtils.roundOff(state.currentPrice * INITIAL_ONE_TIME_SCALE)
                } else {
                    CurrUtils.roundOff(state.currentPrice / INITIAL_RECURRENT_SCALE)
                }
            }
            .map { percent ->
                "5"
            }
    }

    private fun setupFromExisting() =
        intent {
            val pair = pairAlertRepo.getById(pairAlertId!!)!!

            val priceOrPercent =
                pair.percent?.let { percent ->
                    Either.Right(CurrUtils.roundOff(percent.toBigDecimal()))
                } ?: let {
                    Either.Left(
                        if (pair.oneTimeNotRecurrent)
                            CurrUtils.roundOff(pair.targetPrice)
                        else
                            CurrUtils.roundOff(pair.byPriceStep()),
                    )
                }
            val (_, currentPrice) =
                convertUseCase(
                    fromCode = pair.targetCode,
                    toCode = pair.baseCode,
                )
            val state =
                AddPairAlertScreenState(
                    targetCode = pair.targetCode,
                    baseCode = pair.baseCode,
                    priceOrPercent = priceOrPercent,
                    currentPrice = currentPrice,
                    aboveNotBelow = true,
                    group = pair.group,
                    oneTimeNotRecurrent = pair.oneTimeNotRecurrent,
                    editExisting = true,
                )
            reduce { state }
        }

    private fun checkFinishEnabled() =
        intent {
            var enabled = true

            val priceOrPercentNotSuit =
                state.priceOrPercent.fold(
                    ifLeft = { it.toDoubleArk() == 0.0 },
                    ifRight = { it.toDoubleArk() == 0.0 },
                )
            if (priceOrPercentNotSuit)
                enabled = false

            if (state.targetCode == state.baseCode)
                enabled = false

            reduce { state.copy(finishEnabled = enabled) }
        }

    fun onNavigateSearchBase() =
        intent {
            val prohibitedCodes = listOf(state.targetCode)
            postSideEffect(AddPairAlertScreenEffect.NavigateSearchBase(prohibitedCodes))
        }

    fun onNavigateSearchTarget() =
        intent {
            val prohibitedCodes = listOf(state.baseCode)
            postSideEffect(AddPairAlertScreenEffect.NavigateSearchTarget(prohibitedCodes))
        }

    companion object {
        private val INITIAL_ONE_TIME_SCALE = BigDecimal.valueOf(1.1)
        private val INITIAL_RECURRENT_SCALE = BigDecimal.valueOf(10)
    }
}

class AddPairAlertViewModelFactory @AssistedInject constructor(
    @Assisted private val pairAlertId: Long?,
    private val currencyRepo: CurrencyRepo,
    private val pairAlertRepo: PairAlertRepo,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddPairAlertViewModel(
            pairAlertId,
            currencyRepo,
            pairAlertRepo,
            codeUseStatRepo,
            convertUseCase,
            analyticsManager,
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(pairAlertId: Long?): AddPairAlertViewModelFactory
    }
}
