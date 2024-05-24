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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import dev.arkbuilders.rate.presentation.ui.AppHorDiv
import dev.arkbuilders.rate.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.presentation.ui.AppSwipeToDismiss
import dev.arkbuilders.rate.presentation.ui.AppTopBarCenterTitle
import dev.arkbuilders.rate.presentation.ui.GroupViewPager
import org.orbitmvi.orbit.compose.collectAsState
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

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
            Column {
                AppTopBarCenterTitle(title = "Alerts")
                AppHorDiv()
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            if (isEmpty)
                Empty(navigator)
            else {
                Content(
                    state,
                    onDelete = viewModel::onDelete
                )
            }
        }
    }
}

@Composable
private fun Content(state: PairAlertScreenState, onDelete: (PairAlert) -> Unit) {
    Column {
        if (state.pages.size == 1) {
            GroupPage(
                page = state.pages.first(),
                onDelete = { onDelete(it) },
            )
        } else {
            GroupViewPager(
                modifier = Modifier.padding(top = 16.dp),
                groups = state.pages.map { it.group }
            ) { index ->
                GroupPage(
                    page = state.pages[index],
                    onDelete = { onDelete(it) },
                )
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
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (page.created.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp),
                    text = "Created",
                    color = ArkColor.TextTertiary
                )
                AppHorDiv16(modifier = Modifier.padding(top = 12.dp))
            }
            items(page.created, key = { it.id }) {
                AppSwipeToDismiss(
                    content = { PairAlertItem(pairAlert = it) },
                    onDelete = { onDelete(it) }
                )
                AppHorDiv16()
            }
        }
        if (page.oneTimeTriggered.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 25.dp),
                    text = "One-time triggered",
                    color = ArkColor.TextTertiary
                )
                AppHorDiv16(modifier = Modifier.padding(top = 12.dp))
            }
            items(page.oneTimeTriggered, key = { it.id }) {
                AppSwipeToDismiss(
                    content = { PairAlertItem(pairAlert = it) },
                    onDelete = { onDelete(it) }
                )
                AppHorDiv16()
            }
        }
    }
}

@Composable
private fun PairAlertItem(pairAlert: PairAlert) {
    var checkBoxActive by remember {
        mutableStateOf(pairAlert.enabled)
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
            if (pairAlert.oneTimeNotRecurrent && pairAlert.triggered) {
                val date = pairAlert.lastDateTriggered
                date
                    ?: Timber.e("Pair alert marked as triggered but lastDateTriggered is null")
                if (date != null) {
                    val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
                    val monthStr = monthFormat.format(date)
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val timeStr = timeFormat.format(date)
                    Text(
                        text = "Notified on $monthStr ${date.dayOfMonth} - $timeStr",
                        color = ArkColor.TextTertiary
                    )
                }
            }
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
    enabled = true,
    triggered = false,
    group = "Group 1",
    priceNotPercent = true,
    lastDateTriggered = null
)