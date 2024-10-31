package dev.arkbuilders.rate.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.domain.model.Asset
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.domain.repo.PreferenceKey
import dev.arkbuilders.rate.domain.repo.Prefs
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
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
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

data class PortfolioScreenState(
    val filter: String = "",
    val baseCode: CurrencyCode = "USD",
    val pages: List<PortfolioScreenPage> = emptyList(),
    val initialized: Boolean = false,
    val noInternet: Boolean = false,
)

data class PortfolioScreenPage(
    val group: String?,
    val assets: List<PortfolioDisplayAsset>,
)

data class PortfolioDisplayAsset(
    val asset: Asset,
    val baseAmount: Amount,
    val ratioToBase: BigDecimal,
)

sealed class PortfolioScreenEffect {
    class ShowSnackbarAdded(
        val visuals: NotifyAddedSnackbarVisuals,
    ) : PortfolioScreenEffect()

    data class ShowRemovedSnackbar(val asset: Asset) : PortfolioScreenEffect()
}

class PortfolioViewModel(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val prefs: Prefs,
    private val convertUseCase: ConvertWithRateUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModel(), ContainerHost<PortfolioScreenState, PortfolioScreenEffect> {
    override val container: Container<PortfolioScreenState, PortfolioScreenEffect> =
        container(PortfolioScreenState())

    init {
        analyticsManager.trackScreen("PortfolioScreen")

        intent {
            if (currencyRepo.isRatesAvailable().not()) {
                reduce {
                    state.copy(noInternet = true)
                }
                return@intent
            }

            init()
        }
    }

    private fun init() =
        intent {
            initPages()

            AppSharedFlow.ShowAddedSnackbarPortfolio.flow.onEach { visuals ->
                postSideEffect(PortfolioScreenEffect.ShowSnackbarAdded(visuals))
            }.launchIn(viewModelScope)

            prefs.flow(PreferenceKey.BaseCurrencyCode).drop(1).onEach {
                initPages()
            }.launchIn(viewModelScope)

            assetsRepo.allAssetsFlow().drop(1).onEach {
                initPages()
            }.launchIn(viewModelScope)
        }

    fun onRefreshClick() =
        intent {
            reduce { state.copy(noInternet = false) }
            if (currencyRepo.isRatesAvailable()) {
                init()
            } else {
                reduce { state.copy(noInternet = true) }
            }
        }

    fun onAssetRemove(asset: Asset) =
        intent {
            val deleted = assetsRepo.removeAsset(asset.id)
            if (deleted) {
                postSideEffect(PortfolioScreenEffect.ShowRemovedSnackbar(asset))
            }
        }

    fun undoDelete(asset: Asset) =
        intent {
            assetsRepo.setAsset(asset)
        }

    fun onFilterChange(filter: String) =
        blockingIntent {
            reduce { state.copy(filter = filter) }
        }

    private fun initPages() =
        intent {
            val baseCode = prefs.get(PreferenceKey.BaseCurrencyCode)
            val assets = assetsRepo.allAssets().reversed()
            val groups = assets.groupBy(keySelector = { it.group })
            val pages =
                groups.map { (group, assets) ->
                    val displayAssets =
                        assetToPortfolioDisplayAmount(
                            baseCode,
                            assets,
                        )
                    PortfolioScreenPage(group, displayAssets)
                }
            reduce {
                state.copy(baseCode = baseCode, pages = pages, initialized = true)
            }
        }

    private suspend fun assetToPortfolioDisplayAmount(
        baseCode: CurrencyCode,
        list: List<Asset>,
    ): List<PortfolioDisplayAsset> {
        val rates = currencyRepo.getCodeToCurrencyRate().getOrNull()!!
        return list.map { asset ->
            val (baseAmount, rate) =
                convertUseCase(
                    asset.code,
                    asset.value,
                    toCode = baseCode,
                    rates,
                )
            PortfolioDisplayAsset(asset, baseAmount, rate)
        }
    }
}

@Singleton
class PortfolioViewModelFactory @Inject constructor(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val prefs: Prefs,
    private val convertUseCase: ConvertWithRateUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PortfolioViewModel(
            assetsRepo,
            currencyRepo,
            prefs,
            convertUseCase,
            analyticsManager,
        ) as T
    }
}
