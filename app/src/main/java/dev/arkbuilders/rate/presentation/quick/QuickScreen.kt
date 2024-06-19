@file:OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)

package dev.arkbuilders.rate.presentation.quick

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
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
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.presentation.destinations.AddQuickScreenDestination
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.ui.AppButton
import dev.arkbuilders.rate.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.presentation.ui.AppSwipeToDismiss
import dev.arkbuilders.rate.presentation.ui.CurrIcon
import dev.arkbuilders.rate.presentation.ui.CurrencyInfoItem
import dev.arkbuilders.rate.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.presentation.ui.NoResult
import dev.arkbuilders.rate.presentation.ui.NotifyAddedSnackbar
import dev.arkbuilders.rate.presentation.ui.SearchTextField
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

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

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is QuickScreenEffect.ShowSnackbarAdded ->
                snackState.showSnackbar(effect.visuals)
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
                    navigator.navigate(AddQuickScreenDestination())
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
            when {
                state.initialized.not() -> LoadingScreen()
                isEmpty -> QuickEmpty(navigator = navigator)
                else -> Content(
                    state = state,
                    onFilterChanged = viewModel::onFilterChanged,
                    onDelete = viewModel::onDelete,
                    onLongClick = { quick ->
                        navigator.navigate(AddQuickScreenDestination(quickPairId = quick.pair.id))
                    },
                    onNewCode = {
                        navigator.navigate(AddQuickScreenDestination(newCode = it))
                    }
                )
            }
        }
    }
}

@Composable
private fun Content(
    state: QuickScreenState,
    onFilterChanged: (String) -> Unit,
    onDelete: (QuickPair) -> Unit = {},
    onLongClick: (QuickDisplayPair) -> Unit = {},
    onNewCode: (CurrencyCode) -> Unit = {}
) {
    val groups = state.pages.map { it.group }
    Column {
        SearchTextField(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            text = state.filter
        ) {
            onFilterChanged(it)
        }
        if (state.pages.size == 1) {
            GroupPage(
                filter = state.filter,
                currencies = state.currencies,
                quickPairs = state.pages.first().pairs,
                onDelete = onDelete,
                onLongClick = onLongClick,
                onNewCode = onNewCode
            )
        } else {
            GroupViewPager(
                modifier = Modifier.padding(top = 20.dp),
                groups = groups
            ) { index ->
                GroupPage(
                    filter = state.filter,
                    currencies = state.currencies,
                    quickPairs = state.pages[index].pairs,
                    onDelete = onDelete,
                    onLongClick = onLongClick,
                    onNewCode = onNewCode
                )
            }
        }
    }
}

@Composable
private fun GroupPage(
    filter: String,
    currencies: List<CurrencyName>,
    quickPairs: List<QuickDisplayPair>,
    onDelete: (QuickPair) -> Unit,
    onLongClick: (QuickDisplayPair) -> Unit = {},
    onNewCode: (CurrencyCode) -> Unit = {}
) {
    val filteredPairs = quickPairs.filter { displayPair ->
        val containsFrom =
            displayPair.pair.from.contains(filter, ignoreCase = true)
        val containsTo = displayPair.pair.to.any { toCode ->
            toCode.contains(
                filter,
                ignoreCase = true
            )
        }

        containsFrom || containsTo
    }
    val filteredCurrencies = currencies.filter {
        it.name.contains(filter, ignoreCase = true)
                || it.code.contains(filter, ignoreCase = true)
    }
    if (filteredPairs.isNotEmpty() || filteredCurrencies.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (filteredPairs.isNotEmpty()) {
                item {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, top = 24.dp),
                        text = stringResource(R.string.pairs),
                        color = ArkColor.TextTertiary,
                        fontWeight = FontWeight.Medium
                    )
                    AppHorDiv16(modifier = Modifier.padding(top = 12.dp))
                }
                items(filteredPairs, key = { it.pair.id }) {
                    AppSwipeToDismiss(
                        content = { QuickItem(it, onLongClick) },
                        onDelete = { onDelete(it.pair) }
                    )
                    AppHorDiv16()
                }
            }
            if (filteredCurrencies.isNotEmpty()) {
                item {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, top = 24.dp),
                        text = stringResource(R.string.currencies),
                        color = ArkColor.TextTertiary,
                        fontWeight = FontWeight.Medium
                    )
                    AppHorDiv16(modifier = Modifier.padding(top = 12.dp))
                }
                items(filteredCurrencies, key = { it.code }) { name ->
                    CurrencyInfoItem(name) { onNewCode(it.code) }
                }
            }
        }
    } else {
        NoResult()
    }
}

@Preview(showBackground = true)
@Composable
private fun QuickItem(
    quick: QuickDisplayPair = QuickDisplayPair(
        pair = QuickPair(
            0,
            "BTC",
            1.0,
            listOf("USD"),
            null
        ),
        to = emptyList()
    ),
    onLongClick: (QuickDisplayPair) -> Unit = {},
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .combinedClickable(
                onClick = {
                    if (quick.to.size > 1)
                        expanded = !expanded
                },
                onLongClick = {
                    onLongClick(quick)
                }
            )
            .padding(16.dp),
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
                            CurrIcon(
                                modifier = Modifier.size(39.dp),
                                code = quick.pair.to.first()
                            )
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
                    Text(
                        text = "${CurrUtils.prepareToDisplay(quick.pair.amount)} ${quick.pair.from} =",
                        color = ArkColor.TextTertiary
                    )
                    quick.to.forEach {
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
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
        if (quick.to.size > 1) {
            if (expanded) {
                Icon(
                    modifier = Modifier.padding(top = 18.dp, end = 13.dp),
                    painter = painterResource(R.drawable.ic_chevron_up),
                    contentDescription = "",
                    tint = ArkColor.FGSecondary
                )
            } else {
                Icon(
                    modifier = Modifier.padding(top = 18.dp, end = 13.dp),
                    painter = painterResource(R.drawable.ic_chevron),
                    contentDescription = "",
                    tint = ArkColor.FGSecondary
                )
            }
        }
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
                text = stringResource(R.string.quick_empty_title),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = ArkColor.TextPrimary
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
                text = stringResource(R.string.quick_empty_desc),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = ArkColor.TextTertiary,
                textAlign = TextAlign.Center
            )
            AppButton(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    navigator.navigate(AddQuickScreenDestination())
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(R.string.calculate)
                )
            }
        }
    }
}
