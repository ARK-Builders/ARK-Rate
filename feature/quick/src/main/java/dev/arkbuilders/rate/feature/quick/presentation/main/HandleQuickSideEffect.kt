package dev.arkbuilders.rate.feature.quick.presentation.main

import android.content.Context
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.NotifyRemovedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.utils.findActivity
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

// Workaround for SelectTab SideEffect:
// We must wait for Compose to fully render the updated state before calling scrollToPage
// If we proceed too early, the state.pages list might be updated,
// but GroupViewPager may still be rendering with an outdated page count,
// which can lead to IndexOutOfBoundsException when accessing state.pages[index]
@Composable
fun HandleQuickSideEffects(
    viewModel: QuickViewModel,
    state: QuickScreenState,
    pagerState: PagerState,
    snackState: SnackbarHostState,
    ctx: Context,
) {
    val selectTabEffect = remember { mutableStateOf<QuickScreenEffect.SelectTab?>(null) }

    viewModel.collectSideEffect { effect ->
        if (effect is QuickScreenEffect.SelectTab) {
            selectTabEffect.value = effect
        } else {
            handleQuickSideEffect(
                effect = effect,
                viewModel = viewModel,
                snackState = snackState,
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

suspend fun handleQuickSideEffect(
    effect: QuickScreenEffect,
    viewModel: QuickViewModel,
    snackState: SnackbarHostState,
    ctx: Context,
) {
    when (effect) {
        is QuickScreenEffect.ShowSnackbarAdded -> {
            val added =
                ctx.getString(
                    R.string.quick_snackbar_new_added_to,
                    effect.pair.from,
                    effect.pair.to.joinToString { it.code },
                )
            val visuals =
                NotifyAddedSnackbarVisuals(
                    title = ctx.getString(R.string.quick_snackbar_new_title),
                    description =
                        ctx.getString(
                            R.string.quick_snackbar_new_desc,
                            added,
                        ),
                )
            snackState.showSnackbar(visuals)
        }

        is QuickScreenEffect.ShowRemovedSnackbar -> {
            val removed =
                ctx.getString(
                    CoreRString.quick_snackbar_new_added_to,
                    effect.pair.from,
                    effect.pair.to.joinToString { it.code },
                )
            val visuals =
                NotifyRemovedSnackbarVisuals(
                    title = ctx.getString(CoreRString.quick_snackbar_removed_title),
                    description =
                        ctx.getString(
                            CoreRString.quick_snackbar_removed_desc,
                            removed,
                        ),
                    onUndo = {
                        viewModel.undoDelete(effect.pair)
                    },
                )
            snackState.showSnackbar(visuals)
        }

        is QuickScreenEffect.SelectTab -> {
            // Handled in Compose via snapshotFlow to wait for updated state before scrolling
        }

        QuickScreenEffect.NavigateBack -> ctx.findActivity()?.finish()
    }
}
