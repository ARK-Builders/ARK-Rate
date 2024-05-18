@file:OptIn(ExperimentalFoundationApi::class)

package dev.arkbuilders.rate.presentation.portfolio

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import dev.arkbuilders.rate.data.model.CurrencyAmount
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.AddCurrencyScreenDestination
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.theme.ArkTypography
import dev.arkbuilders.rate.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.presentation.ui.SearchTextFieldWithSort
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination
@Composable
fun PortfolioScreen(navigator: DestinationsNavigator) {
    val viewModel: PortfolioViewModel =
        viewModel(factory = DIManager.component.assetsVMFactory())

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect {

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
                    navigator.navigate(AddCurrencyScreenDestination)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "")
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            if (isEmpty)
                PortfolioEmpty(navigator)
            else
                Content(state)
        }
    }
}

private val previewPortfolioAmount = PortfolioDisplayAmount(
    CurrencyAmount(code = "EUR", amount = 1100.2),
    CurrencyAmount(code = "USD", amount = 1200.0),
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
private fun Content(state: PortfolioScreenState = previewState) {
    val groupToAmounts = state.groupToPortfolioAmount.toList()
    val groups = groupToAmounts.map { it.first }
    Column {
        SearchTextFieldWithSort(modifier = Modifier.padding(top = 16.dp))
        GroupViewPager(modifier = Modifier.padding(top = 16.dp), groups = groups) {
            GroupPage(
                baseCode = state.baseCode,
                amounts = groupToAmounts.map { it.second }[it]
            )
        }
    }
}

@Composable
private fun GroupPage(baseCode: CurrencyCode, amounts: List<PortfolioDisplayAmount>) {
    val total = amounts.fold(0.0) { acc, amount ->
        acc + amount.baseAmount.amount
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp),
            thickness = 1.dp,
            color = ArkColor.BorderSecondary
        )
        amounts.forEach {
            CurrencyItem(it)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyItem(
    amount: PortfolioDisplayAmount = previewPortfolioAmount,
    onClick: (PortfolioDisplayAmount) -> Unit = {},
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .clickable {

                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_earth),
                contentDescription = "",
                tint = Color.Unspecified
            )
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = amount.amount.code,
                        fontWeight = FontWeight.Medium,
                        color = ArkColor.TextPrimary
                    )
                    Text(
                        text = "${CurrUtils.prepareToDisplay(amount.baseAmount.amount)} ${amount.baseAmount.code}",
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
                        text = "${CurrUtils.prepareToDisplay(amount.amount.amount)} ${amount.amount.code}",
                        color = ArkColor.TextTertiary
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = ArkColor.BorderSecondary
        )
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
                painter = painterResource(id = R.drawable.ic_quick_empty),
                contentDescription = "",
                tint = ArkColor.Secondary,
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
                    navigator.navigate(AddCurrencyScreenDestination)
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
