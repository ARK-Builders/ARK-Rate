package dev.arkbuilders.rate.feature.pairalert.presentation.main

import android.Manifest
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.ramcosta.composedestinations.generated.pairalert.destinations.AddPairAlertScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.NotifyRemovedSnackbarVisuals
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
fun HandlePairAlertSideEffect(
    state: PairAlertScreenState,
    navigator: DestinationsNavigator,
    viewModel: PairAlertViewModel,
    pagerState: PagerState,
    snackState: SnackbarHostState,
    onScreenOpenNotificationPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    onNewPairNotificationPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    ctx: Context,
    getCurrentGroup: () -> Group?,
) {
    val selectTabEffect = remember { mutableStateOf<PairAlertEffect.SelectTab?>(null) }

    viewModel.collectSideEffect { effect ->
        if (effect is PairAlertEffect.SelectTab) {
            selectTabEffect.value = effect
        } else {
            handlePairAlertSideEffect(
                effect,
                navigator,
                viewModel,
                snackState,
                onScreenOpenNotificationPermissionLauncher,
                onNewPairNotificationPermissionLauncher,
                ctx,
                getCurrentGroup,
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

suspend fun handlePairAlertSideEffect(
    effect: PairAlertEffect,
    navigator: DestinationsNavigator,
    viewModel: PairAlertViewModel,
    snackState: SnackbarHostState,
    onScreenOpenNotificationPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    onNewPairNotificationPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    ctx: Context,
    getCurrentGroup: () -> Group?,
) {
    when (effect) {
        is PairAlertEffect.NavigateToAdd ->
            navigator.navigate(
                AddPairAlertScreenDestination(
                    pairAlertId = effect.pairId,
                    groupId = getCurrentGroup()?.id,
                ),
            )

        PairAlertEffect.AskNotificationPermissionOnScreenOpen -> {
            onScreenOpenNotificationPermissionLauncher
                .launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        PairAlertEffect.AskNotificationPermissionOnNewPair -> {
            onNewPairNotificationPermissionLauncher
                .launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        is PairAlertEffect.SelectTab -> {
            // Handled in Compose via snapshotFlow to wait for updated state before scrolling
        }

        is PairAlertEffect.ShowSnackbarAdded -> {
            val pair = effect.pair
            val aboveOrBelow =
                if (pair.above())
                    ctx.getString(CoreRString.above)
                else
                    ctx.getString(CoreRString.below)
            val visuals =
                NotifyAddedSnackbarVisuals(
                    title =
                        ctx.getString(
                            CoreRString.alert_snackbar_new_title,
                            pair.targetCode,
                        ),
                    description =
                        ctx.getString(
                            CoreRString.alert_snackbar_new_desc,
                            pair.targetCode,
                            aboveOrBelow,
                            CurrUtils.prepareToDisplay(pair.targetPrice),
                            pair.baseCode,
                        ),
                )
            snackState.showSnackbar(visuals)
        }

        is PairAlertEffect.ShowRemovedSnackbar -> {
            val visuals =
                NotifyRemovedSnackbarVisuals(
                    title =
                        ctx.getString(
                            CoreRString.alert_snackbar_removed_title,
                            effect.pair.targetCode,
                        ),
                    description =
                        ctx.getString(
                            CoreRString.alert_snackbar_removed_desc,
                            effect.pair.targetCode,
                            effect.pair.baseCode,
                        ),
                    onUndo = {
                        viewModel.undoDelete(effect.pair)
                    },
                )
            snackState.showSnackbar(visuals)
        }
    }
}
