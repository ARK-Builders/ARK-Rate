@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.presentation.pairalert

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.data.model.PairAlert
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.AddPairAlertScreenDestination
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.theme.ArkTypography
import dev.arkbuilders.rate.presentation.ui.AppSwipeToDismiss
import dev.arkbuilders.rate.presentation.ui.AppTopBarCenterTitle
import dev.arkbuilders.rate.presentation.ui.GroupViewPager
import org.orbitmvi.orbit.compose.collectAsState

@Destination
@Composable
fun PairAlertConditionScreen(
    navigator: DestinationsNavigator,
) {
    val viewModel: PairAlertViewModel =
        viewModel(factory = DIManager.component.pairAlertVMFactory())

    val state by viewModel.collectAsState()

    val isEmpty = state.pages.isEmpty()

    Scaffold(
        floatingActionButton = {
            if (isEmpty)
                return@Scaffold

            FloatingActionButton(
                contentColor = Color.White,
                containerColor = ArkColor.Secondary,
                onClick = {
                    navigator.navigate(AddPairAlertScreenDestination)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "")
            }
        },
        topBar = {
            if (isEmpty) return@Scaffold
            AppTopBarCenterTitle(title = "Alerts")
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            if (isEmpty)
                Empty(navigator)
            else {
                GroupViewPager(groups = state.pages.map { it.group }) { index ->
                    GroupPage(
                        page = state.pages[index],
                        onDelete = { viewModel.onDelete(it) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun GroupPage(
    page: PairAlertScreenPage = PairAlertScreenPage(
        group = "Group 1",
        created = listOf(previewPairAlert),
        oneTimeTriggered = listOf(previewPairAlert)
    ), onDelete: (PairAlert) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 24.dp),
            text = "Created",
            color = ArkColor.TextTertiary
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, end = 16.dp),
            color = ArkColor.BorderSecondary
        )
        page.created.forEach {
            AppSwipeToDismiss(
                content = { PairAlertItem(pairAlert = it) },
                onDelete = { onDelete(it) }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = ArkColor.BorderSecondary
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 25.dp),
            text = "One-time triggered",
            color = ArkColor.TextTertiary
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, end = 16.dp),
            color = ArkColor.BorderSecondary
        )
        page.oneTimeTriggered.forEach {
            AppSwipeToDismiss(
                content = { PairAlertItem(pairAlert = it) },
                onDelete = { onDelete(it) }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = ArkColor.BorderSecondary
            )
        }
    }
}

@Composable
private fun PairAlertItem(pairAlert: PairAlert) {
    var checkBoxActive by remember {
        mutableStateOf(true)
    }
    var currencyName by remember {
        mutableStateOf("")
    }
    val currencyRepo = DIManager.component.generalCurrencyRepo()
    LaunchedEffect(Unit) {
        currencyName = currencyRepo.currencyNameByCode(pairAlert.targetCode).name
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
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
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$currencyName(${pairAlert.targetCode}) ${if (pairAlert.oneTimeNotRecurrent) "(One-time)" else ""}",
                fontWeight = FontWeight.Medium,
                color = ArkColor.TextPrimary
            )
            Text(
                text = "${if (pairAlert.targetPrice > pairAlert.startPrice) "Above" else "Below"} " +
                        "${CurrUtils.prepareToDisplay(pairAlert.targetPrice)} " +
                        "${pairAlert.baseCode}",
                color = ArkColor.TextTertiary
            )
        }
        Switch(
            checked = checkBoxActive,
            onCheckedChange = { checkBoxActive = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedBorderColor = ArkColor.Primary,
                checkedTrackColor = ArkColor.Primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = ArkColor.BGTertiary,
                uncheckedBorderColor = ArkColor.BGTertiary
            )
        )
    }

}


@Composable
private fun Empty(navigator: DestinationsNavigator) {
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
                text = "No Alerts at the Moment",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
                text = "Stay updated! We'll post any important notifications or changes in exchange rates here.",
                style = ArkTypography.supporting,
                textAlign = TextAlign.Center
            )
            Button(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    navigator.navigate(AddPairAlertScreenDestination)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = ""
                )
                Text(text = "New Alert")
            }
        }
    }
}

private val previewPairAlert = PairAlert(
    id = 0,
    targetCode = "USD",
    baseCode = "EUR",
    targetPrice = 2.0,
    startPrice = 1.0,
    alertPercent = null,
    oneTimeNotRecurrent = true,
    triggered = false,
    group = "Group 1",
    priceNotPercent = true
)