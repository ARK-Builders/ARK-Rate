package dev.arkbuilders.rate.feature.quick.presentation.main

import android.content.Context
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.SnackbarHostState
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.NotifyRemovedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.utils.findActivity

suspend fun handleQuickSideEffect(
    effect: QuickScreenEffect,
    state: QuickScreenState,
    viewModel: QuickViewModel,
    pagerState: PagerState,
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
            val page = state.pages.find { it.group.id == effect.groupId }!!
            val pageIndex = state.pages.indexOf(page)
            pagerState.scrollToPage(pageIndex)
        }

        QuickScreenEffect.NavigateBack -> ctx.findActivity()?.finish()
    }
}
