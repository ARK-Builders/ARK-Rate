package dev.arkbuilders.rate.feature.pairalert.presentation.main

import android.Manifest
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.SnackbarHostState
import com.ramcosta.composedestinations.generated.pairalert.destinations.AddPairAlertScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.NotifyRemovedSnackbarVisuals

suspend fun handlePairAlertSideEffect(
    effect: PairAlertEffect,
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
            val page = state.pages.find { it.group.id == effect.groupId }!!
            val pageIndex = state.pages.indexOf(page)
            pagerState.scrollToPage(pageIndex)
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
