@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.portfolio.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.generated.portfolio.destinations.AddAssetScreenDestination
import com.ramcosta.composedestinations.generated.portfolio.destinations.EditAssetScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.result.onResult
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.core.presentation.ui.AppSwipeToDismiss
import dev.arkbuilders.rate.core.presentation.ui.CurrIcon
import dev.arkbuilders.rate.core.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.core.presentation.ui.LargeNumberText
import dev.arkbuilders.rate.core.presentation.ui.LargeNumberTooltipBox
import dev.arkbuilders.rate.core.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.core.presentation.ui.NoResult
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.NotifyRemovedSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.RateSnackbarHost
import dev.arkbuilders.rate.core.presentation.ui.SearchTextField
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupOptionsBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRenameBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupReorderBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRow
import dev.arkbuilders.rate.feature.portfolio.di.PortfolioComponentHolder
import dev.arkbuilders.rate.feature.portfolio.domain.model.Asset
import dev.arkbuilders.rate.feature.portfolio.presentation.model.AddAssetsNavResult
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.math.BigDecimal

@Destination<ExternalModuleGraph>
@Composable
fun PortfolioScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<AddAssetScreenDestination, AddAssetsNavResult>,
) {
    val ctx = LocalContext.current
    val component =
        remember {
            PortfolioComponentHolder.provide(ctx)
        }

    val viewModel: PortfolioViewModel =
        viewModel(factory = component.assetsVMFactory())

    resultRecipient.onResult {
        viewModel.onReturnFromAddScreen(it)
    }

    BackHandler {
        viewModel.onBackClick()
    }

    val state by viewModel.collectAsState()
    val snackState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isEmpty = state.pages.isEmpty()
    val pagerState = rememberPagerState { state.pages.size }
    val editGroupReorderSheetState = rememberModalBottomSheetState()
    val editGroupOptionsSheetState = rememberModalBottomSheetState()
    val editGroupRenameSheetState = rememberModalBottomSheetState()

    fun getCurrentGroup() = state.pages.getOrNull(pagerState.currentPage)?.group

    viewModel.collectSideEffect { effect ->
        when (effect) {
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
                    navigator.navigate(AddAssetScreenDestination(groupId = getCurrentGroup()?.id))
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
                state.initialized.not() -> LoadingScreen()
                isEmpty -> PortfolioEmpty(navigator)
                else ->
                    Content(
                        state = state,
                        pagerState = pagerState,
                        onEditGroups = viewModel::onShowGroupsReorder,
                        onClick = { display ->
                            navigator
                                .navigate(EditAssetScreenDestination(display.asset.id))
                        },
                        onFilterChange = viewModel::onFilterChange,
                        onDelete = viewModel::onAssetRemove,
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
            PortfolioComponentHolder.provide(ctx).validateGroupNameUseCase()
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
    state: PortfolioScreenState,
    pagerState: PagerState,
    onEditGroups: () -> Unit,
    onClick: (PortfolioDisplayAsset) -> Unit,
    onFilterChange: (String) -> Unit,
    onDelete: (Asset) -> Unit,
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
            onValueChange = onFilterChange,
        )
        if (state.pages.size == 1) {
            GroupPage(
                filter = state.filter,
                baseCode = state.baseCode,
                amounts = state.pages.first().assets,
                onClick = onClick,
                onDelete = onDelete,
            )
        } else {
            EditGroupRow(
                onEdit = onEditGroups,
            )
            Spacer(Modifier.height(4.dp))
            GroupViewPager(
                pagerState = pagerState,
                groups = groups,
            ) { index ->
                GroupPage(
                    filter = state.filter,
                    baseCode = state.baseCode,
                    amounts = state.pages[index].assets,
                    onClick = onClick,
                    onDelete = onDelete,
                )
            }
        }
    }
}

@Composable
private fun GroupPage(
    filter: String,
    baseCode: CurrencyCode,
    amounts: List<PortfolioDisplayAsset>,
    onClick: (PortfolioDisplayAsset) -> Unit = {},
    onDelete: (Asset) -> Unit,
) {
    val total =
        amounts.fold(BigDecimal.ZERO) { acc, amount ->
            acc + amount.baseAmount.value
        }
    val filtered =
        amounts.filter { it.asset.code.contains(filter, ignoreCase = true) }
    if (filtered.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (filter.isEmpty()) {
                item {
                    Text(
                        modifier = Modifier.padding(top = 32.dp),
                        text = stringResource(CoreRString.portfolio_total_assets),
                        color = ArkColor.TextTertiary,
                        fontWeight = FontWeight.Medium,
                    )
                    Row(
                        modifier =
                            Modifier
                                .padding(top = 8.dp, start = 24.dp, end = 24.dp),
                    ) {
                        LargeNumberTooltipBox(number = total, code = baseCode) {
                            LargeNumberText(
                                number = total,
                                color = ArkColor.TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 36.sp,
                            )
                        }
                        Text(
                            modifier =
                                Modifier
                                    .padding(
                                        start = 2.dp,
                                        top = 2.dp,
                                    )
                                    .weight(1f, fill = false),
                            text = CurrUtils.getSymbolOrCode(baseCode),
                            color = ArkColor.TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                        )
                    }
                    AppHorDiv16(Modifier.padding(top = 32.dp))
                }
            }
            items(filtered, key = { it.asset.id }) {
                AppSwipeToDismiss(
                    content = { CurrencyItem(it, onClick = onClick) },
                    onDelete = { onDelete(it.asset) },
                )
                AppHorDiv16()
            }
        }
    } else {
        NoResult()
    }
}

@Composable
private fun CurrencyItem(
    amount: PortfolioDisplayAsset,
    onClick: (PortfolioDisplayAsset) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable {
                    onClick(amount)
                }
                .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CurrIcon(modifier = Modifier.size(40.dp), code = amount.asset.code)
        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = amount.asset.code,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary,
                )
                LargeNumberTooltipBox(
                    modifier = Modifier.weight(1f),
                    number = amount.baseAmount.value,
                    code = amount.baseAmount.code,
                ) {
                    LargeNumberText(
                        number = amount.baseAmount.value,
                        code = amount.baseAmount.code,
                        fontWeight = FontWeight.Medium,
                        color = ArkColor.TextPrimary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = CurrUtils.prepareToDisplay(amount.ratioToBase),
                    color = ArkColor.TextTertiary,
                )
                LargeNumberTooltipBox(
                    modifier = Modifier.weight(1f),
                    number = amount.asset.value,
                    code = amount.asset.code,
                ) {
                    LargeNumberText(
                        number = amount.asset.value,
                        code = amount.asset.code,
                        color = ArkColor.TextTertiary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioEmpty(navigator: DestinationsNavigator) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = CoreRDrawable.ic_empty_portfolio),
                contentDescription = "",
                tint = Color.Unspecified,
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(CoreRString.portfolio_empty_title),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = ArkColor.TextPrimary,
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
                text = stringResource(CoreRString.portfolio_empty_desc),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = ArkColor.TextTertiary,
                textAlign = TextAlign.Center,
            )
            AppButton(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    navigator.navigate(AddAssetScreenDestination())
                },
            ) {
                Icon(
                    painter = painterResource(id = CoreRDrawable.ic_add),
                    contentDescription = "",
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(CoreRString.portfolio_empty_new_assets),
                )
            }
        }
    }
}
