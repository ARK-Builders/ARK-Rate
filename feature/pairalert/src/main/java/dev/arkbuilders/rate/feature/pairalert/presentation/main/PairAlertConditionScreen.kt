@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.pairalert.presentation.main

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.generated.pairalert.destinations.AddPairAlertScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.result.onResult
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.core.presentation.ui.AppSwipeToDismiss
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarCenterTitle
import dev.arkbuilders.rate.core.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.core.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.core.presentation.ui.RateSnackbarHost
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupOptionsBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRenameBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupReorderBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRow
import dev.arkbuilders.rate.feature.pairalert.di.PairAlertComponent
import dev.arkbuilders.rate.feature.pairalert.di.PairAlertComponentHolder
import dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

@Destination<ExternalModuleGraph>
@Composable
fun PairAlertConditionScreen(
    navigator: DestinationsNavigator,
    // expect new pair id
    resultRecipient: ResultRecipient<AddPairAlertScreenDestination, Long>,
) {
    val ctx = LocalContext.current
    val component =
        remember {
            PairAlertComponentHolder.provide(ctx)
        }
    val viewModel: PairAlertViewModel =
        viewModel(factory = component.pairAlertVMFactory())

    resultRecipient.onResult {
        viewModel.onReturnFromAddScreen(it)
    }

    val onScreenOpenNotificationPermissionLauncher = rememberNotificationPermissionLauncher(ctx)
    val onNewPairNotificationPermissionLauncher =
        rememberNotificationPermissionLauncher(
            ctx = ctx,
            onGranted = viewModel::onNotificationPermissionGrantedOnNewPair,
        )

    val state by viewModel.collectAsState()
    val snackState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()
    val isEmpty = state.pages.isEmpty()
    val pagerState = rememberPagerState { state.pages.size }
    val editGroupReorderSheetState = rememberModalBottomSheetState()
    val editGroupOptionsSheetState = rememberModalBottomSheetState()
    val editGroupRenameSheetState = rememberModalBottomSheetState()

    fun getCurrentGroup() = state.pages.getOrNull(pagerState.currentPage)?.group

    HandlePairAlertSideEffect(
        state,
        navigator,
        viewModel,
        pagerState,
        snackState,
        onScreenOpenNotificationPermissionLauncher,
        onNewPairNotificationPermissionLauncher,
        ctx,
        ::getCurrentGroup,
    )

    Scaffold(
        floatingActionButton = {
            if (state.initialized.not())
                return@Scaffold

            if (isEmpty)
                return@Scaffold

            FloatingActionButton(
                contentColor = Color.White,
                containerColor = ArkColor.Secondary,
                shape = CircleShape,
                onClick = viewModel::onNewPair,
            ) {
                Icon(Icons.Default.Add, contentDescription = "")
            }
        },
        topBar = {
            if (isEmpty) return@Scaffold
            AppTopBarCenterTitle(title = stringResource(CoreRString.alerts))
        },
        snackbarHost = {
            RateSnackbarHost(snackState)
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            when {
                state.initialized.not() -> LoadingScreen()
                isEmpty -> PairAlertEmpty(onNewPair = viewModel::onNewPair)
                else ->
                    Content(
                        component = component,
                        state = state,
                        pagerState = pagerState,
                        onEditGroups = viewModel::onShowGroupsReorder,
                        onDelete = viewModel::onDelete,
                        onClick = { pair ->
                            viewModel.onNewPair(pair.id)
                        },
                        onEnableToggle = viewModel::onEnableToggle,
                    )
            }
        }
    }

    state.editGroupReorderSheetState?.let {
        EditGroupReorderBottomSheet(
            sheetState = editGroupReorderSheetState,
            state = it,
            onSwap = { from, to -> viewModel.onSwapGroups(from, to) },
            onOptionsClick = { viewModel.onShowGroupOptions(it) },
            onDismiss = {
                scope.launch {
                    editGroupReorderSheetState.hide()
                    viewModel.onDismissGroupsReorder()
                }
            },
        )
    }
    state.editGroupOptionsSheetState?.let {
        EditGroupOptionsBottomSheet(
            sheetState = editGroupOptionsSheetState,
            state = it,
            onRename = { viewModel.onShowGroupRename(it.group) },
            onDelete = { viewModel.onGroupDelete(it.group) },
            onDismiss = {
                scope.launch {
                    editGroupOptionsSheetState.hide()
                    viewModel.onDismissGroupOptions()
                }
            },
        )
    }
    val validateGroupNameUseCase =
        remember {
            PairAlertComponentHolder.provide(ctx).validateGroupNameUseCase()
        }
    state.editGroupRenameSheetState?.let { renameState ->
        EditGroupRenameBottomSheet(
            sheetState = editGroupRenameSheetState,
            state = renameState,
            validateGroupNameUseCase = validateGroupNameUseCase,
            onDone = { viewModel.onGroupRename(renameState.group, it) },
            onDismiss = {
                scope.launch {
                    editGroupRenameSheetState.hide()
                    viewModel.onDismissGroupRename()
                }
            },
        )
    }
}

@Composable
private fun Content(
    component: PairAlertComponent,
    state: PairAlertScreenState,
    pagerState: PagerState,
    onEditGroups: () -> Unit,
    onDelete: (PairAlert) -> Unit,
    onClick: (PairAlert) -> Unit,
    onEnableToggle: (PairAlert, Boolean) -> Unit,
) {
    Column {
        if (state.pages.size == 1) {
            GroupPage(
                component = component,
                page = state.pages.first(),
                onDelete = { onDelete(it) },
                onClick = onClick,
                onEnableToggle = onEnableToggle,
            )
        } else {
            EditGroupRow(onEdit = onEditGroups)
            GroupViewPager(
                modifier = Modifier.padding(top = 16.dp),
                pagerState = pagerState,
                groups = state.pages.map { it.group },
            ) { index ->
                GroupPage(
                    component = component,
                    page = state.pages[index],
                    onDelete = { onDelete(it) },
                    onClick = onClick,
                    onEnableToggle = onEnableToggle,
                )
            }
        }
    }
}

@Composable
private fun GroupPage(
    component: PairAlertComponent,
    page: PairAlertScreenPage,
    onDelete: (PairAlert) -> Unit,
    onClick: (PairAlert) -> Unit,
    onEnableToggle: (PairAlert, Boolean) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (page.created.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp),
                    text = "Created",
                    color = ArkColor.TextTertiary,
                )
                AppHorDiv16(modifier = Modifier.padding(top = 12.dp))
            }
            items(page.created, key = { it.id }) {
                AppSwipeToDismiss(
                    content = {
                        PairAlertItem(
                            component = component,
                            pairAlert = it,
                            oneTimeTriggered = false,
                            onClick = onClick,
                            onEnableToggle = onEnableToggle,
                        )
                    },
                    onDelete = { onDelete(it) },
                )
                AppHorDiv16()
            }
        }
        if (page.oneTimeTriggered.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 25.dp),
                    text = "One-time triggered",
                    color = ArkColor.TextTertiary,
                )
                AppHorDiv16(modifier = Modifier.padding(top = 12.dp))
            }
            items(page.oneTimeTriggered, key = { it.id }) {
                AppSwipeToDismiss(
                    content = {
                        PairAlertItem(
                            component = component,
                            pairAlert = it,
                            oneTimeTriggered = true,
                            onClick = onClick,
                            onEnableToggle = onEnableToggle,
                        )
                    },
                    onDelete = { onDelete(it) },
                )
                AppHorDiv16()
            }
        }
    }
}

@Composable
private fun rememberNotificationPermissionLauncher(
    ctx: Context,
    onGranted: (() -> Unit)? = null,
) = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission(),
) { isGranted ->
    if (isGranted) {
        onGranted?.invoke()
    } else {
        Toast.makeText(
            ctx,
            ctx.getString(CoreRString.alert_post_notification_permission_explanation),
            Toast.LENGTH_SHORT,
        ).show()
    }
}
