package dev.arkbuilders.rate.feature.portfolio.presentation.main

import android.content.Context
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.SnackbarHostState
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.NotifyRemovedSnackbarVisuals
import java.math.BigDecimal

suspend fun handlePortfolioSideEffect(
    effect: PortfolioScreenEffect,
    viewModel: PortfolioViewModel,
    navigator: DestinationsNavigator,
    state: PortfolioScreenState,
    pagerState: PagerState,
    snackState: SnackbarHostState,
    ctx: Context,
) {
    when (effect) {
        is PortfolioScreenEffect.SelectTab -> {
            val page = state.pages.find { it.group.id == effect.groupId }!!
            val pageIndex = state.pages.indexOf(page)
            pagerState.scrollToPage(pageIndex)
        }

        is PortfolioScreenEffect.ShowSnackbarAdded -> {
            val added =
                effect.assets
                    .joinToString {
                        "${CurrUtils.prepareToDisplay(BigDecimal(it.value))} ${it.code}"
                    }
            val visuals =
                NotifyAddedSnackbarVisuals(
                    ctx.getString(CoreRString.portfolio_snackbar_new_title),
                    ctx.getString(
                        CoreRString.portfolio_snackbar_new_desc,
                        added,
                    ),
                )

            snackState.showSnackbar(visuals)
        }

        is PortfolioScreenEffect.ShowRemovedSnackbar -> {
            val removed =
                CurrUtils.prepareToDisplay(effect.asset.value) +
                    " ${effect.asset.code}"
            val visuals =
                NotifyRemovedSnackbarVisuals(
                    title = ctx.getString(CoreRString.portfolio_snackbar_removed_title),
                    description =
                        ctx.getString(
                            CoreRString.portfolio_snackbar_removed_desc,
                            removed,
                        ),
                    onUndo = {
                        viewModel.undoDelete(effect.asset)
                    },
                )
            snackState.showSnackbar(visuals)
        }

        PortfolioScreenEffect.NavigateBack -> navigator.popBackStack()
    }
}
