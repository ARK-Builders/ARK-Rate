package dev.arkbuilders.rate.feature.portfolio.presentation.main

import android.content.Context
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.NotifyRemovedSnackbarVisuals
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber
import java.math.BigDecimal

// Workaround for SelectTab SideEffect:
// We must wait for Compose to fully render the updated state before calling scrollToPage
// If we proceed too early, the state.pages list might be updated,
// but GroupViewPager may still be rendering with an outdated page count,
// which can lead to IndexOutOfBoundsException when accessing state.pages[index]
@Composable
fun HandlePortfolioSideEffect(
    viewModel: PortfolioViewModel,
    navigator: DestinationsNavigator,
    state: PortfolioScreenState,
    pagerState: PagerState,
    snackState: SnackbarHostState,
    ctx: Context,
) {
    val selectTabEffect = remember { mutableStateOf<PortfolioScreenEffect.SelectTab?>(null) }

    viewModel.collectSideEffect { effect ->
        if (effect is PortfolioScreenEffect.SelectTab) {
            selectTabEffect.value = effect
        } else {
            handlePortfolioSideEffect(
                effect = effect,
                viewModel = viewModel,
                snackState = snackState,
                navigator = navigator,
                ctx = ctx,
            )
        }
    }

    LaunchedEffect(selectTabEffect.value) {
        val effect = selectTabEffect.value ?: return@LaunchedEffect

        // Wait until the page with the target groupId appears
        snapshotFlow { state.pages }
            .filter { it.find { page -> page.group.id == effect.groupId } != null }
            .first()

        // Skip scrolling if there's only one page
        if (state.pages.size == 1) {
            selectTabEffect.value = null
            return@LaunchedEffect
        }

        val index = state.pages.indexOfFirst { it.group.id == effect.groupId }
        if (index >= 0) {
            pagerState.scrollToPage(index)
        } else {
            Timber.e(
                "Scroll to tab failed: groupId=${effect.groupId} not found in pages. " +
                    "Pages=${state.pages.map { it.group.id }}",
            )
        }

        selectTabEffect.value = null
    }
}

suspend fun handlePortfolioSideEffect(
    effect: PortfolioScreenEffect,
    viewModel: PortfolioViewModel,
    navigator: DestinationsNavigator,
    snackState: SnackbarHostState,
    ctx: Context,
) {
    when (effect) {
        is PortfolioScreenEffect.SelectTab -> {
            // Handled in Compose via snapshotFlow to wait for updated state before scrolling
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
