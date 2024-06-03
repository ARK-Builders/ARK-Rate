@file:OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)

package dev.arkbuilders.rate.presentation.quick

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.AddQuickScreenDestination
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.theme.ArkTypography
import dev.arkbuilders.rate.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.presentation.ui.AppSwipeToDismiss
import dev.arkbuilders.rate.presentation.ui.CurrIcon
import dev.arkbuilders.rate.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.presentation.ui.NotifyAddedSnackbar
import dev.arkbuilders.rate.presentation.ui.SearchTextFieldWithSort
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.compose.collectAsState

@RootNavGraph(start = true)
@Destination
@Composable
fun QuickScreen(
    navigator: DestinationsNavigator,
) {
    val viewModel: QuickViewModel = viewModel(
        factory = DIManager.component.quickVMFactory().create()
    )

    val state by viewModel.collectAsState()
    val snackState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        AppSharedFlow.ShowAddedSnackbarQuick.flow.onEach { visuals ->
            visuals ?: return@onEach
            snackState.showSnackbar(visuals)
            AppSharedFlow.ShowAddedSnackbarQuick.flow.emit(null)
        }.launchIn(scope)
    }

    val isEmpty = state.groupToQuickPairs.isEmpty()

    Scaffold(
        floatingActionButton = {
            if (isEmpty)
                return@Scaffold

            FloatingActionButton(
                contentColor = Color.White,
                containerColor = ArkColor.Secondary,
                onClick = {
                    navigator.navigate(AddQuickScreenDestination)
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
                QuickEmpty(navigator = navigator)
            else
                Content(state = state, onDelete = viewModel::onDelete)
        }
    }
}

@Composable
private fun Content(state: QuickScreenState, onDelete: (QuickPair) -> Unit = {}) {
    val groups = state.groupToQuickPairs.map { it.first }
    Column {
        SearchTextFieldWithSort(modifier = Modifier.padding(top = 16.dp))
        if (state.groupToQuickPairs.size == 1) {
            GroupPage(
                quickPairs = state.groupToQuickPairs.first().second,
                onDelete = onDelete
            )
        } else {
            GroupViewPager(
                modifier = Modifier.padding(top = 20.dp),
                groups = groups
            ) {
                GroupPage(
                    quickPairs = state.groupToQuickPairs[it].second,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun GroupPage(
    quickPairs: List<DisplayQuickPair>,
    onDelete: (QuickPair) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 24.dp),
                text = "Pairs",
                color = ArkColor.TextTertiary,
                fontWeight = FontWeight.Medium
            )
        }
        items(quickPairs, key = { it.pair.id }) {
            AppHorDiv16()
            AppSwipeToDismiss(
                content = { QuickItem(it) },
                onDelete = { onDelete(it.pair) }
            )
            AppHorDiv16()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuickItem(
    quick: DisplayQuickPair = DisplayQuickPair(
        pair = QuickPair(
            0,
            "BTC",
            1.0,
            listOf("USD"),
            null
        ),
        to = emptyList()
    )
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
            .clickable { expanded = !expanded },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                ) {
                    CurrIcon(modifier = Modifier.size(40.dp), code = quick.pair.from)
                }
                if (!expanded) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .offset((-12).dp)
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        if (quick.to.size == 1) {
                            CurrIcon(modifier = Modifier.size(39.dp), code = quick.pair.to.first())
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(ArkColor.BGTertiary, CircleShape)
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = "+ ${quick.to.size}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    color = ArkColor.TextTertiary
                                )
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.padding(start = if (expanded) 12.dp else 0.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${quick.pair.from} to ${quick.pair.to.joinToString(", ")}",
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary
                )
                if (expanded) {
                    Box(modifier = Modifier.height(4.dp))
                    quick.to.forEach {
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CurrIcon(modifier = Modifier.size(20.dp), code = it.code)
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = "${CurrUtils.prepareToDisplay(it.value)} ${it.code}",
                                color = ArkColor.TextTertiary
                            )
                        }
                    }
                } else {
                    Text(
                        text = "${CurrUtils.prepareToDisplay(quick.pair.amount)} ${quick.pair.from} = " +
                                "${CurrUtils.prepareToDisplay(quick.to.first().value)} ${quick.to.first().code}",
                        color = ArkColor.TextTertiary
                    )
                }
            }
        }
        Icon(
            painter = painterResource(R.drawable.ic_chevron),
            contentDescription = "",
            tint = ArkColor.FGSecondary
        )
    }

}

@Composable
private fun QuickEmpty(navigator: DestinationsNavigator) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_empty_quick),
                contentDescription = "",
                tint = Color.Unspecified,
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "Ready for calculation!",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
                text = "Select your currencies and enter an amount to start converting. Your exchange results will appear here.",
                style = ArkTypography.supporting,
                textAlign = TextAlign.Center
            )
            Button(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    navigator.navigate(AddQuickScreenDestination)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = ""
                )
                Text(text = "New Pair")
            }
        }
    }
}
