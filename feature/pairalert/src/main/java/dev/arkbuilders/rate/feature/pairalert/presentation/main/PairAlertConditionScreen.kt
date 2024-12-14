@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.pairalert.presentation.main

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.core.presentation.ui.AppSwipeToDismiss
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarCenterTitle
import dev.arkbuilders.rate.core.presentation.ui.CurrIcon
import dev.arkbuilders.rate.core.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.core.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.core.presentation.ui.NoInternetScreen
import dev.arkbuilders.rate.core.presentation.ui.NotifyRemovedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.RateSnackbarHost
import dev.arkbuilders.rate.core.presentation.utils.DateFormatUtils
import dev.arkbuilders.rate.feature.pairalert.di.PairAlertComponent
import dev.arkbuilders.rate.feature.pairalert.di.PairAlertComponentHolder
import dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert
import dev.arkbuilders.rate.feature.pairalert.presentation.destinations.AddPairAlertScreenDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

@Destination
@Composable
fun PairAlertConditionScreen(navigator: DestinationsNavigator) {
    val ctx = LocalContext.current
    val component =
        remember {
            PairAlertComponentHolder.provide(ctx)
        }
    val viewModel: PairAlertViewModel =
        viewModel(factory = component.pairAlertVMFactory())

    val onScreenOpenNotificationPermissionLauncher = rememberNotificationPermissionLauncher(ctx)
    val onNewPairNotificationPermissionLauncher =
        rememberNotificationPermissionLauncher(
            ctx = ctx,
            onGranted = viewModel::onNotificationPermissionGrantedOnNewPair,
        )

    val state by viewModel.collectAsState()
    val snackState = remember { SnackbarHostState() }

    val isEmpty = state.pages.isEmpty()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is PairAlertEffect.NavigateToAdd ->
                navigator.navigate(
                    AddPairAlertScreenDestination(
                        pairAlertId = effect.pairId,
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

            is PairAlertEffect.ShowSnackbarAdded ->
                snackState.showSnackbar(effect.visuals)

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
                state.noInternet -> NoInternetScreen(viewModel::onRefreshClick)
                state.initialized.not() -> LoadingScreen()
                isEmpty -> Empty(onNewPair = viewModel::onNewPair)
                else ->
                    Content(
                        component = component,
                        state = state,
                        onDelete = viewModel::onDelete,
                        onClick = { pair ->
                            viewModel.onNewPair(pair.id)
                        },
                        onEnableToggle = viewModel::onEnableToggle,
                    )
            }
        }
    }
}

@Composable
private fun Content(
    component: PairAlertComponent,
    state: PairAlertScreenState,
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
            GroupViewPager(
                modifier = Modifier.padding(top = 16.dp),
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
private fun PairAlertItem(
    component: PairAlertComponent,
    pairAlert: PairAlert,
    oneTimeTriggered: Boolean,
    onClick: (PairAlert) -> Unit,
    onEnableToggle: (PairAlert, Boolean) -> Unit,
) {
    var currencyName by remember {
        mutableStateOf("")
    }
    val currencyRepo = component.currencyRepo()
    LaunchedEffect(Unit) {
        currencyName = currencyRepo.nameByCodeUnsafe(pairAlert.targetCode).name
    }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable {
                    onClick(pairAlert)
                }
                .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CurrIcon(modifier = Modifier.size(40.dp), code = pairAlert.targetCode)
        Column(
            modifier =
                Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text =
                    "$currencyName(${pairAlert.targetCode}) " +
                        if (pairAlert.oneTimeNotRecurrent) "(One-time)" else "",
                fontWeight = FontWeight.Medium,
                color = ArkColor.TextPrimary,
            )
            Text(
                text =
                    buildString {
                        append(
                            "${
                                if (pairAlert.above())
                                    stringResource(CoreRString.above_c)
                                else
                                    stringResource(CoreRString.below_c)
                            } ",
                        )
                        append("${CurrUtils.prepareToDisplay(pairAlert.targetPrice)} ")
                        append(pairAlert.baseCode)
                    },
                color = ArkColor.TextTertiary,
            )
            if (oneTimeTriggered) {
                val date = pairAlert.lastDateTriggered
                date
                    ?: Timber.e("Pair alert marked as triggered but lastDateTriggered is null")
                if (date != null) {
                    Text(
                        text =
                            stringResource(
                                CoreRString.alert_notified_on,
                                DateFormatUtils.notifiedOn(date),
                            ),
                        color = ArkColor.TextTertiary,
                    )
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = pairAlert.enabled,
            onCheckedChange = { onEnableToggle(pairAlert, it) },
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedBorderColor = ArkColor.Primary,
                    checkedTrackColor = ArkColor.Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = ArkColor.BGTertiary,
                    uncheckedBorderColor = ArkColor.BGTertiary,
                ),
        )
    }
}

@Composable
private fun Empty(onNewPair: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = CoreRDrawable.ic_empty_pair),
                contentDescription = "",
                tint = Color.Unspecified,
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(CoreRString.alert_empty_title),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = ArkColor.TextPrimary,
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
                text = stringResource(CoreRString.alert_empty_desc),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = ArkColor.TextTertiary,
                textAlign = TextAlign.Center,
            )
            AppButton(
                modifier = Modifier.padding(top = 24.dp),
                onClick = { onNewPair() },
            ) {
                Icon(
                    painter = painterResource(id = CoreRDrawable.ic_add),
                    contentDescription = "",
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(CoreRString.new_alert),
                )
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

private val previewPairAlert =
    PairAlert(
        id = 0,
        targetCode = "USD",
        baseCode = "EUR",
        targetPrice = 2.0.toBigDecimal(),
        startPrice = 1.0.toBigDecimal(),
        percent = null,
        oneTimeNotRecurrent = true,
        enabled = true,
        group = "Group 1",
        lastDateTriggered = null,
    )
