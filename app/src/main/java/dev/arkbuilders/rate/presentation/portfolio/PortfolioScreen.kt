@file:OptIn(ExperimentalFoundationApi::class)

package dev.arkbuilders.rate.presentation.portfolio

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.domain.model.Asset
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.presentation.destinations.AddAssetScreenDestination
import dev.arkbuilders.rate.presentation.destinations.EditAssetScreenDestination
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.ui.AppButton
import dev.arkbuilders.rate.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.presentation.ui.AppSwipeToDismiss
import dev.arkbuilders.rate.presentation.ui.CurrIcon
import dev.arkbuilders.rate.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.presentation.ui.NoInternetScreen
import dev.arkbuilders.rate.presentation.ui.NoResult
import dev.arkbuilders.rate.presentation.ui.NotifyRemovedSnackbarVisuals
import dev.arkbuilders.rate.presentation.ui.RateSnackbarHost
import dev.arkbuilders.rate.presentation.ui.SearchTextField
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination
@Composable
fun PortfolioScreen(navigator: DestinationsNavigator) {
    val viewModel: PortfolioViewModel =
        viewModel(factory = DIManager.component.assetsVMFactory())

    val state by viewModel.collectAsState()
    val snackState = remember { SnackbarHostState() }
    val ctx = LocalContext.current

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is PortfolioScreenEffect.ShowSnackbarAdded ->
                snackState.showSnackbar(effect.visuals)

            is PortfolioScreenEffect.ShowRemovedSnackbar -> {
                val removed =
                    CurrUtils.prepareToDisplay(effect.asset.value) +
                        " ${effect.asset.code}"
                val visuals =
                    NotifyRemovedSnackbarVisuals(
                        title = ctx.getString(R.string.portfolio_snackbar_removed_title),
                        description =
                            ctx.getString(
                                R.string.portfolio_snackbar_removed_desc,
                                removed,
                            ),
                        onUndo = {
                            viewModel.undoDelete(effect.asset)
                        },
                    )
                snackState.showSnackbar(visuals)
            }
        }
    }

    val isEmpty = state.pages.isEmpty()

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
                    navigator.navigate(AddAssetScreenDestination)
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
                isEmpty -> PortfolioEmpty(navigator)
                else ->
                    Content(
                        state,
                        onClick = { display ->
                            navigator.navigate(EditAssetScreenDestination(display.asset.id))
                        },
                        onFilterChange = viewModel::onFilterChange,
                        onDelete = viewModel::onAssetRemove,
                    )
            }
        }
    }
}

private val previewPortfolioAmount =
    PortfolioDisplayAsset(
        Asset(code = "EUR", value = 1100.2),
        Amount(code = "USD", value = 1200.0),
        ratioToBase = 1.1,
    )

private val previewState =
    PortfolioScreenState(
        pages =
            listOf(
                PortfolioScreenPage(
                    "Group1",
                    listOf(previewPortfolioAmount, previewPortfolioAmount),
                ),
                PortfolioScreenPage(
                    "Group2",
                    listOf(previewPortfolioAmount, previewPortfolioAmount),
                ),
            ),
    )

@Preview(showBackground = true)
@Composable
private fun Content(
    state: PortfolioScreenState = previewState,
    onClick: (PortfolioDisplayAsset) -> Unit = {},
    onFilterChange: (String) -> Unit = {},
    onDelete: (Asset) -> Unit = {},
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
            GroupViewPager(
                modifier = Modifier.padding(top = 20.dp),
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
        amounts.fold(0.0) { acc, amount ->
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
                        text = stringResource(R.string.portfolio_total_assets),
                        color = ArkColor.TextTertiary,
                        fontWeight = FontWeight.Medium,
                    )
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            modifier = Modifier,
                            text = CurrUtils.prepareToDisplay(total),
                            color = ArkColor.TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 36.sp,
                        )
                        Text(
                            modifier = Modifier.padding(start = 2.dp, top = 2.dp),
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

@Preview(showBackground = true)
@Composable
private fun CurrencyItem(
    amount: PortfolioDisplayAsset = previewPortfolioAmount,
    onClick: (PortfolioDisplayAsset) -> Unit = {},
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
                    text = amount.asset.code,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary,
                )
                Text(
                    text = "${CurrUtils.prepareToDisplay(
                        amount.baseAmount.value,
                    )} ${amount.baseAmount.code}",
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = CurrUtils.prepareToDisplay(amount.ratioToBase),
                    color = ArkColor.TextTertiary,
                )
                Text(
                    text = "${CurrUtils.prepareToDisplay(amount.asset.value)} ${amount.asset.code}",
                    color = ArkColor.TextTertiary,
                )
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
                painter = painterResource(id = R.drawable.ic_empty_portfolio),
                contentDescription = "",
                tint = Color.Unspecified,
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(R.string.portfolio_empty_title),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = ArkColor.TextPrimary,
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
                text = stringResource(R.string.portfolio_empty_desc),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = ArkColor.TextTertiary,
                textAlign = TextAlign.Center,
            )
            AppButton(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    navigator.navigate(AddAssetScreenDestination)
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "",
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(R.string.new_asset),
                )
            }
        }
    }
}
