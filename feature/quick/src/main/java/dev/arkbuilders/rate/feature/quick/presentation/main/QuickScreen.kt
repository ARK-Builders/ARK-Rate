package dev.arkbuilders.rate.feature.quick.presentation.main

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.generated.quick.destinations.AddQuickScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.result.onResult
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyName
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.core.presentation.ui.CurrencyInfoItem
import dev.arkbuilders.rate.core.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.core.presentation.ui.ListHeader
import dev.arkbuilders.rate.core.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.core.presentation.ui.RateSnackbarHost
import dev.arkbuilders.rate.core.presentation.ui.SearchTextField
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupOptionsBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRenameBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupReorderBottomSheet
import dev.arkbuilders.rate.core.presentation.utils.DateFormatUtils
import dev.arkbuilders.rate.feature.quick.di.QuickComponentHolder
import dev.arkbuilders.rate.feature.quick.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import dev.arkbuilders.rate.feature.quick.presentation.ui.PinnedQuickSwipeItem
import dev.arkbuilders.rate.feature.quick.presentation.ui.QuickOptionsBottomSheet
import dev.arkbuilders.rate.feature.quick.presentation.ui.QuickSwipeItem
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Destination<ExternalModuleGraph>
@Composable
fun QuickScreen(
    navigator: DestinationsNavigator,
    // expect new pair id
    resultRecipient: ResultRecipient<AddQuickScreenDestination, Long>,
) {
    val ctx = LocalContext.current
    val component =
        remember {
            QuickComponentHolder.provide(ctx)
        }
    val viewModel: QuickViewModel =
        viewModel(
            factory = component.quickVMFactory().create(),
        )

    resultRecipient.onResult(
        onCancelled = viewModel::onNavResultCancelled,
        onValue = viewModel::onNavResultValue,
    )

    BackHandler {
        viewModel.onBackClick()
    }

    val state by viewModel.collectAsState()
    val pagerState = rememberPagerState { state.pages.size }
    val snackState = remember { SnackbarHostState() }

    val isEmpty = state.pages.isEmpty()

    val scope = rememberCoroutineScope()
    val pairOptionsSheetState = rememberModalBottomSheetState()
    val editGroupReorderSheetState = rememberModalBottomSheetState()
    val editGroupOptionsSheetState = rememberModalBottomSheetState()
    val editGroupRenameSheetState = rememberModalBottomSheetState()

    fun getCurrentGroup() = state.pages.getOrNull(pagerState.currentPage)?.group

    HandleQuickSideEffects(
        viewModel = viewModel,
        state = state,
        pagerState = pagerState,
        snackState = snackState,
        ctx = ctx,
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
                onClick = {
                    navigator.navigate(
                        AddQuickScreenDestination(
                            groupId = getCurrentGroup()?.id,
                        ),
                    )
                },
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(CoreRString.add))
            }
        },
        snackbarHost = {
            RateSnackbarHost(snackState)
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            when {
                state.initialized.not() -> LoadingScreen()
                isEmpty -> QuickEmpty(navigator)
                else ->
                    Content(
                        state = state,
                        pagerState = pagerState,
                        onEditGroups = viewModel::onShowGroupsReorder,
                        onFilterChanged = viewModel::onFilterChanged,
                        onDelete = viewModel::onDelete,
                        onClick = {
                            viewModel.onShowGroupOptions(it)
                        },
                        onPin = viewModel::onPin,
                        onUnpin = viewModel::onUnpin,
                        onNewCode = {
                            navigator
                                .navigate(
                                    AddQuickScreenDestination(
                                        newCode = it,
                                        groupId = getCurrentGroup()?.id,
                                    ),
                                )
                        },
                    )
            }
        }
        state.pairOptionsData?.let {
            QuickOptionsBottomSheet(
                pairOptionsSheetState,
                pair = it.pair,
                onPin = viewModel::onPin,
                onUnpin = viewModel::onUnpin,
                onEdit = {
                    navigator.navigate(
                        AddQuickScreenDestination(
                            quickPairId = it.id,
                            reuseNotEdit = false,
                            groupId = getCurrentGroup()?.id,
                        ),
                    )
                },
                onReuse = {
                    navigator.navigate(
                        AddQuickScreenDestination(
                            quickPairId = it.id,
                            groupId = getCurrentGroup()?.id,
                        ),
                    )
                },
                onDelete = viewModel::onDelete,
                onDismiss = {
                    scope.launch {
                        pairOptionsSheetState.hide()
                        viewModel.onHideOptions()
                    }
                },
            )
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
                QuickComponentHolder.provide(ctx).validateGroupNameUseCase()
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
}

@Composable
private fun Content(
    state: QuickScreenState,
    pagerState: PagerState,
    onEditGroups: () -> Unit,
    onFilterChanged: (String) -> Unit,
    onDelete: (QuickPair) -> Unit,
    onClick: (QuickPair) -> Unit,
    onPin: (QuickPair) -> Unit,
    onUnpin: (QuickPair) -> Unit,
    onNewCode: (CurrencyCode) -> Unit,
) {
    val groups = state.pages.map { it.group }
    Column {
        SearchTextField(
            modifier =
                Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
            text = state.filter,
        ) {
            onFilterChanged(it)
        }
        if (state.filter.isNotEmpty()) {
            QuickSearchPage(
                filter = state.filter,
                topResults = state.topResults,
                onNewCode = onNewCode,
            )
        } else {
            if (state.pages.size == 1) {
                GroupPage(
                    frequent = state.frequent,
                    currencies = state.currencies,
                    pinned = state.pages.first().pinned,
                    notPinned = state.pages.first().notPinned,
                    onDelete = onDelete,
                    onClick = onClick,
                    onPin = onPin,
                    onUnpin = onUnpin,
                    onNewCode = onNewCode,
                )
            } else {
                GroupViewPager(
                    pagerState = pagerState,
                    groups = groups,
                    onEditGroups = onEditGroups,
                ) { index ->
                    GroupPage(
                        frequent = state.frequent,
                        currencies = state.currencies,
                        pinned = state.pages[index].pinned,
                        notPinned = state.pages[index].notPinned,
                        onDelete = onDelete,
                        onClick = onClick,
                        onPin = onPin,
                        onUnpin = onUnpin,
                        onNewCode = onNewCode,
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupPage(
    frequent: List<CurrencyName>,
    currencies: List<CurrencyName>,
    pinned: List<PinnedQuickPair>,
    notPinned: List<QuickPair>,
    onDelete: (QuickPair) -> Unit,
    onPin: (QuickPair) -> Unit,
    onUnpin: (QuickPair) -> Unit,
    onClick: (QuickPair) -> Unit = {},
    onNewCode: (CurrencyCode) -> Unit,
) {
    val ctx = LocalContext.current
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (pinned.isNotEmpty()) {
            item {
                ListHeader(text = stringResource(CoreRString.quick_pinned_calculations))
            }
            items(pinned, key = { it.pair.id }) {
                PinnedQuickSwipeItem(
                    content = {
                        QuickItem(
                            from = Amount(it.pair.from, it.pair.amount),
                            to = it.actualTo,
                            dateText =
                                stringResource(
                                    CoreRString.quick_last_refreshed,
                                    DateFormatUtils.latestCheckElapsedTime(
                                        ctx,
                                        OffsetDateTime.now(),
                                        it.refreshDate,
                                    ),
                                ),
                            onClick = { onClick(it.pair) },
                        )
                    },
                    pair = it.pair,
                    onDelete = { onDelete(it.pair) },
                    onUnpin = onUnpin,
                )
                AppHorDiv16()
            }
        }
        if (notPinned.isNotEmpty()) {
            item {
                ListHeader(text = stringResource(CoreRString.quick_calculations))
            }
            items(notPinned, key = { it.id }) {
                QuickSwipeItem(
                    content = {
                        QuickItem(
                            from = Amount(it.from, it.amount),
                            to = it.to,
                            dateText =
                                stringResource(
                                    CoreRString.quick_calculated_on,
                                    DateFormatUtils.calculatedOn(it.calculatedDate),
                                ),
                            onClick = { onClick(it) },
                        )
                    },
                    pair = it,
                    onDelete = { onDelete(it) },
                    onPin = onPin,
                )
                AppHorDiv16()
            }
        }
        if (frequent.isNotEmpty()) {
            item {
                ListHeader(text = stringResource(CoreRString.frequent_currencies))
            }
            items(frequent) { name ->
                CurrencyInfoItem(name) { onNewCode(it.code) }
            }
        }
        item {
            ListHeader(text = stringResource(CoreRString.all_currencies))
        }
        items(currencies, key = { it.code }) { name ->
            CurrencyInfoItem(name) { onNewCode(it.code) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewItem() {
    QuickItem(
        from = Amount("BTC", 1.0.toBigDecimal()),
        to = listOf(Amount("USD", 30.0.toBigDecimal())),
        dateText = "Calculated on",
        onClick = {},
    )
}
