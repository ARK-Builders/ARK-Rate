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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import dev.arkbuilders.rate.domain.model.Asset
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.presentation.destinations.AddAssetScreenDestination
import dev.arkbuilders.rate.presentation.destinations.EditAssetScreenDestination
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.theme.ArkTypography
import dev.arkbuilders.rate.presentation.ui.NotifyAddedSnackbar
import dev.arkbuilders.rate.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.presentation.ui.AppSwipeToDismiss
import dev.arkbuilders.rate.presentation.ui.CurrIcon
import dev.arkbuilders.rate.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.presentation.ui.SearchTextFieldWithSort
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination
@Composable
fun PortfolioScreen(navigator: DestinationsNavigator) {
    val viewModel: PortfolioViewModel =
        viewModel(factory = DIManager.component.assetsVMFactory())

    val state by viewModel.collectAsState()
    val snackState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    viewModel.collectSideEffect {

    }

    LaunchedEffect(key1 = Unit) {
        AppSharedFlow.ShowAddedSnackbarPortfolio.flow.onEach { visuals ->
            visuals ?: return@onEach
            snackState.showSnackbar(visuals)
            AppSharedFlow.ShowAddedSnackbarPortfolio.flow.emit(null)
        }.launchIn(scope)
    }

    val isEmpty = state.groupToPortfolioAmount.isEmpty()

    Scaffold(
        floatingActionButton = {
            if (isEmpty)
                return@Scaffold

            FloatingActionButton(
                contentColor = Color.White,
                containerColor = ArkColor.Secondary,
                onClick = {
                    navigator.navigate(AddAssetScreenDestination)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "")
            }
        },
        snackbarHost = {
            NotifyAddedSnackbar(snackState = snackState)
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            if (isEmpty)
                PortfolioEmpty(navigator)
            else
                Content(
                    state,
                    onClick = { display ->
                        navigator.navigate(EditAssetScreenDestination(display.asset.id))
                    },
                    onDelete = viewModel::onAssetRemove
                )
        }
    }
}

private val previewPortfolioAmount = PortfolioDisplayAsset(
    Asset(code = "EUR", value = 1100.2),
    Amount(code = "USD", value = 1200.0),
    ratioToBase = 1.1
)

private val previewState = PortfolioScreenState(
    groupToPortfolioAmount = mapOf(
        "Group1" to listOf(previewPortfolioAmount, previewPortfolioAmount),
        "Group2" to listOf(previewPortfolioAmount, previewPortfolioAmount),
    )
)

@Preview(showBackground = true)
@Composable
private fun Content(
    state: PortfolioScreenState = previewState,
    onClick: (PortfolioDisplayAsset) -> Unit = {},
    onDelete: (Asset) -> Unit = {}
) {
    val groupToAmounts = state.groupToPortfolioAmount.toList()
    val groups = groupToAmounts.map { it.first }
    Column {
        SearchTextFieldWithSort(modifier = Modifier.padding(top = 16.dp))
        if (groupToAmounts.size == 1) {
            GroupPage(
                baseCode = state.baseCode,
                amounts = groupToAmounts.map { it.second }.first(),
                onClick = onClick,
                onDelete = onDelete
            )
        } else {
            GroupViewPager(
                modifier = Modifier.padding(top = 16.dp),
                groups = groups
            ) {
                GroupPage(
                    baseCode = state.baseCode,
                    amounts = groupToAmounts.map { it.second }[it],
                    onClick = onClick,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun GroupPage(
    baseCode: CurrencyCode,
    amounts: List<PortfolioDisplayAsset>,
    onClick: (PortfolioDisplayAsset) -> Unit = {},
    onDelete: (Asset) -> Unit
) {
    val total = amounts.fold(0.0) { acc, amount ->
        acc + amount.baseAmount.value
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = "Total Assets",
                color = ArkColor.TextTertiary,
                fontWeight = FontWeight.Medium
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "${CurrUtils.prepareToDisplay(total)} $baseCode",
                color = ArkColor.TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 36.sp
            )
            AppHorDiv16(Modifier.padding(top = 32.dp))
        }
        items(amounts, key = { it.asset.id }) {
            AppSwipeToDismiss(
                content = { CurrencyItem(it, onClick = onClick) },
                onDelete = { onDelete(it.asset) }
            )
            AppHorDiv16()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyItem(
    amount: PortfolioDisplayAsset = previewPortfolioAmount,
    onClick: (PortfolioDisplayAsset) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .clickable {
                onClick(amount)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrIcon(modifier = Modifier.size(40.dp), code = amount.asset.code)
        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = amount.asset.code,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary
                )
                Text(
                    text = "${CurrUtils.prepareToDisplay(amount.baseAmount.value)} ${amount.baseAmount.code}",
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = CurrUtils.prepareToDisplay(amount.ratioToBase),
                    color = ArkColor.TextTertiary
                )
                Text(
                    text = "${CurrUtils.prepareToDisplay(amount.asset.value)} ${amount.asset.code}",
                    color = ArkColor.TextTertiary
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_empty_portfolio),
                contentDescription = "",
                tint = Color.Unspecified,
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "Your Portfolio is Empty",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
                text = "Add currencies manually to track and compare. Your portfolio helps you monitor and manage your favorite or frequently used currencies.",
                style = ArkTypography.supporting,
                textAlign = TextAlign.Center
            )
            Button(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    navigator.navigate(AddAssetScreenDestination)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = ""
                )
                Text(text = "New Asset")
            }
        }
    }
}
