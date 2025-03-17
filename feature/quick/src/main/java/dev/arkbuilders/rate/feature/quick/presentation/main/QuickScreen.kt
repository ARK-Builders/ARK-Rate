package dev.arkbuilders.rate.feature.quick.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyName
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.core.presentation.ui.CurrIcon
import dev.arkbuilders.rate.core.presentation.ui.CurrencyInfoItem
import dev.arkbuilders.rate.core.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.core.presentation.ui.ListHeader
import dev.arkbuilders.rate.core.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.core.presentation.ui.NoInternetScreen
import dev.arkbuilders.rate.core.presentation.ui.NoResult
import dev.arkbuilders.rate.core.presentation.ui.NotifyRemovedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.RateSnackbarHost
import dev.arkbuilders.rate.core.presentation.ui.SearchTextField
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupOptionsBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRenameBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupReorderBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRow
import dev.arkbuilders.rate.core.presentation.utils.DateFormatUtils
import dev.arkbuilders.rate.core.presentation.utils.findActivity
import dev.arkbuilders.rate.feature.quick.di.QuickComponentHolder
import dev.arkbuilders.rate.feature.quick.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import dev.arkbuilders.rate.feature.quick.presentation.destinations.AddQuickScreenDestination
import dev.arkbuilders.rate.feature.quick.presentation.ui.PinnedQuickSwipeItem
import dev.arkbuilders.rate.feature.quick.presentation.ui.QuickOptionsBottomSheet
import dev.arkbuilders.rate.feature.quick.presentation.ui.QuickSwipeItem
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun QuickScreen(navigator: DestinationsNavigator) {
    val ctx = LocalContext.current
    val component =
        remember {
            QuickComponentHolder.provide(ctx)
        }
    val viewModel: QuickViewModel =
        viewModel(
            factory = component.quickVMFactory().create(),
        )

    BackHandler {
        viewModel.onBackClick()
    }

    val state by viewModel.collectAsState()
    val snackState = remember { SnackbarHostState() }
    viewModel.collectSideEffect { effect ->
        when (effect) {
            is QuickScreenEffect.ShowSnackbarAdded ->
                snackState.showSnackbar(effect.visuals)

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

            QuickScreenEffect.NavigateBack -> ctx.findActivity()?.finish()
        }
    }

    val isEmpty = state.pages.isEmpty()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { state.pages.size }
    val pairOptionsSheetState = rememberModalBottomSheetState()
    val editGroupReorderSheetState = rememberModalBottomSheetState()
    val editGroupOptionsSheetState = rememberModalBottomSheetState()
    val editGroupRenameSheetState = rememberModalBottomSheetState()

    fun getCurrentGroup() = state.pages.getOrNull(pagerState.currentPage)?.group

    Scaffold(
        floatingActionButton = {
            if (state.initialized.not())
                return@Scaffold

            if (state.noInternet)
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
                Icon(Icons.Default.Add, contentDescription = "")
            }
        },
        snackbarHost = {
            RateSnackbarHost(snackState)
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            when {
                state.noInternet -> NoInternetScreen(viewModel::onRefreshClick)
                state.initialized.not() -> LoadingScreen()
                isEmpty -> QuickEmpty(navigator)
                else ->
                    Content(
                        state = state,
                        pagerState = pagerState,
                        onEdit = viewModel::onShowGroupsReorder,
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
        state.editGroupRenameSheetState?.let { renameState ->
            EditGroupRenameBottomSheet(
                sheetState = editGroupRenameSheetState,
                state = renameState,
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
    onEdit: () -> Unit,
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
            SearchPage(
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
                EditGroupRow(onEdit)
                Spacer(Modifier.height(4.dp))
                GroupViewPager(
                    pagerState = pagerState,
                    groups = groups,
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
                ListHeader(text = stringResource(CoreRString.quick_pinned_pairs))
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

@Composable
private fun SearchPage(
    filter: String,
    topResults: List<CurrencyName>,
    onNewCode: (CurrencyCode) -> Unit,
) {
    val filtered =
        topResults.filter {
            it.name.contains(filter, ignoreCase = true) ||
                it.code.contains(filter, ignoreCase = true)
        }
    if (filtered.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ListHeader(text = stringResource(CoreRString.top_results))
            }
            items(filtered) { name ->
                CurrencyInfoItem(name) { onNewCode(it.code) }
            }
        }
    } else {
        NoResult()
    }
}

@Composable
private fun QuickItem(
    from: Amount,
    to: List<Amount>,
    dateText: String,
    onClick: () -> Unit,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ConstraintLayout(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable {
                    onClick()
                },
    ) {
        val (icons, content, chevron) = createRefs()
        Row(
            modifier =
                Modifier.constrainAs(icons) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp),
            ) {
                CurrIcon(modifier = Modifier.size(40.dp), code = from.code)
            }
            if (!expanded) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .offset((-12).dp)
                            .border(2.dp, Color.White, CircleShape),
                ) {
                    if (to.size == 1) {
                        CurrIcon(
                            modifier =
                                Modifier
                                    .size(38.dp)
                                    .align(Alignment.Center)
                                    .clip(CircleShape)
                                    .background(Color.White),
                            code = to.first().code,
                        )
                    } else {
                        Box(
                            modifier =
                                Modifier
                                    .size(40.dp)
                                    .background(ArkColor.BGTertiary, CircleShape),
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "+ ${to.size}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = ArkColor.TextTertiary,
                            )
                        }
                    }
                }
            }
        }
        Column(
            modifier =
                Modifier
                    .constrainAs(content) {
                        start.linkTo(icons.end)
                        if (to.size > 1)
                            end.linkTo(chevron.start)
                        else
                            end.linkTo(parent.end, margin = 24.dp)
                        top.linkTo(parent.top, margin = 16.dp)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = if (expanded) 12.dp else 0.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text =
                    "${from.code} to " +
                        to.joinToString(", ") { it.code },
                fontWeight = FontWeight.Medium,
                color = ArkColor.TextPrimary,
            )
            if (expanded) {
                Text(
                    text = "${CurrUtils.prepareToDisplay(from.value)} ${from.code} =",
                    color = ArkColor.TextTertiary,
                )
                to.forEach {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CurrIcon(modifier = Modifier.size(20.dp), code = it.code)
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "${CurrUtils.prepareToDisplay(it.value)} ${it.code}",
                            color = ArkColor.TextTertiary,
                        )
                    }
                }
            } else {
                Text(
                    text =
                        "${CurrUtils.prepareToDisplay(from.value)} ${from.code} = " +
                            "${CurrUtils.prepareToDisplay(to.first().value)} ${to.first().code}",
                    color = ArkColor.TextTertiary,
                )
            }
            Text(
                modifier = Modifier.padding(top = if (expanded) 8.dp else 0.dp),
                text = dateText,
                color = ArkColor.TextTertiary,
                fontSize = 12.sp,
            )
        }
        if (to.size > 1) {
            Box(
                modifier =
                    Modifier
                        .constrainAs(chevron) {
                            height = Dimension.fillToConstraints
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .clickable {
                            expanded = !expanded
                        }
                        .padding(start = 13.dp, end = 29.dp, top = 23.dp),
            ) {
                if (expanded) {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(CoreRDrawable.ic_chevron_up),
                        contentDescription = "",
                        tint = ArkColor.FGSecondary,
                    )
                } else {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(CoreRDrawable.ic_chevron),
                        contentDescription = "",
                        tint = ArkColor.FGSecondary,
                    )
                }
            }
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

@Composable
private fun QuickEmpty(navigator: DestinationsNavigator) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = CoreRDrawable.ic_empty_quick),
                contentDescription = "",
                tint = Color.Unspecified,
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(CoreRString.quick_empty_title),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = ArkColor.TextPrimary,
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
                text = stringResource(CoreRString.quick_empty_desc),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = ArkColor.TextTertiary,
                textAlign = TextAlign.Center,
            )
            AppButton(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    navigator.navigate(AddQuickScreenDestination())
                },
            ) {
                Icon(
                    painter = painterResource(id = CoreRDrawable.ic_add),
                    contentDescription = "",
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(CoreRString.calculate),
                )
            }
        }
    }
}
