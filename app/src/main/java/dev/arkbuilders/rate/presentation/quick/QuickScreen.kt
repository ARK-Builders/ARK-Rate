@file:OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)

package dev.arkbuilders.rate.presentation.quick

import androidx.annotation.StringRes
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.CurrencyAmount
import dev.arkbuilders.rate.data.preferences.PreferenceKey
import dev.arkbuilders.rate.data.preferences.QuickScreenShowAs
import dev.arkbuilders.rate.data.preferences.QuickScreenSortedBy
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.AddCurrencyScreenDestination
import dev.arkbuilders.rate.presentation.destinations.QuickScreenDestination
import dev.arkbuilders.rate.presentation.destinations.SummaryScreenDestination
import dev.arkbuilders.rate.presentation.shared.SharedViewModel
import dev.arkbuilders.rate.presentation.utils.activityViewModel
import dev.arkbuilders.rate.presentation.utils.collectInLaunchedEffectWithLifecycle
import dev.arkbuilders.rate.utils.removeFractionalPartIfEmpty
import eu.wewox.tagcloud.TagCloud
import eu.wewox.tagcloud.TagCloudItemScope
import eu.wewox.tagcloud.rememberTagCloudState
import kotlinx.coroutines.launch

@Destination
@Composable
fun QuickScreen(
    navigator: DestinationsNavigator,
    sharedViewModel: SharedViewModel = activityViewModel(),
) {
    val viewModel: QuickViewModel = viewModel(
        factory = DIManager.component.quickVMFactory().create(sharedViewModel)
    )

    viewModel.navigateToSummary.collectInLaunchedEffectWithLifecycle { amount ->
        navigator.navigate(SummaryScreenDestination(amount))
    }

    SortDialog(viewModel)

    if (viewModel.selectedCurrency == null) {
        SelectQuickCurrency(navigator, viewModel)
    } else {
        InputAmount(viewModel, viewModel.selectedCurrency!!)
    }
}

@Composable
private fun SelectQuickCurrency(
    navigator: DestinationsNavigator,
    viewModel: QuickViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.fillMaxWidth()) {
            ShowAsToggle(Modifier.align(Alignment.Center), viewModel)
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { viewModel.sortDialogVisible = true }
            ) {
                Icon(
                    painterResource(R.drawable.ic_sort),
                    tint = MaterialTheme.colors.secondary,
                    contentDescription = ""
                )
            }
        }
        when (viewModel.showAs) {
            QuickScreenShowAs.TAG_CLOUD -> {
                val tagCloudState = rememberTagCloudState()
                viewModel.currencyAttractionList.let { list ->
                    if (list.isNotEmpty()) {
                        TagCloud(
                            state = tagCloudState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(64.dp)
                        ) {
                            items(list) {
                                CloudQuickItem(viewModel, it)
                            }
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }

            QuickScreenShowAs.LIST -> {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        viewModel.currencyAttractionList
                            .sortedByDescending { it.attractionRatio },
                        key = { it.code }
                    ) {
                        ListQuickItem(
                            viewModel,
                            it
                        )
                    }
                }
            }

            QuickScreenShowAs.GRID -> {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .wrapContentSize()
                        .weight(1f),
                    columns = StaggeredGridCells.Fixed(3),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(
                        viewModel.currencyAttractionList,
                        key = { it.code }
                    ) {
                        GridQuickItem(viewModel, it)
                    }
                }
            }
        }
        Button(
            modifier = Modifier
                .padding(10.dp)
                .wrapContentSize(),
            colors = ButtonDefaults
                .buttonColors(backgroundColor = MaterialTheme.colors.secondary),
            onClick = {
                navigator.navigate(
                    AddCurrencyScreenDestination(
                        fromScreen = QuickScreenDestination.route
                    )
                )
            }
        ) {
            val iconWidth = remember { 32.dp }
            Row {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "",
                    modifier = Modifier.width(iconWidth)
                )
                Text(text = "Search")
            }
        }
    }
}

@Composable
private fun InputAmount(
    viewModel: QuickViewModel,
    amount: CurrencyAmount,
) {
    var amountInput by remember {
        mutableStateOf(
            if (amount.amount == 0.0) ""
            else amount.amount.removeFractionalPartIfEmpty()
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = amountInput,
            onValueChange = { newInput ->
                amountInput = viewModel.onAmountChanged(
                    amountInput,
                    newInput
                )
            },
            label = { Text(viewModel.selectedCurrency!!.code) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            maxLines = 1,
        )
        OutlinedButton(
            modifier = Modifier.padding(8.dp),
            onClick = {
                viewModel.onExchange()
            }
        ) {
            Text(text = "Convert", fontSize = 20.sp)
        }
    }
}

private const val minSize = 70
private const val maxSize = 140
private const val minFontSize = 12
private const val maxFontSize = 20
private const val sizeDiff = maxSize - minSize
private const val fontSizeDiff = maxFontSize - minFontSize

@Composable
private fun TagCloudItemScope.CloudQuickItem(
    viewModel: QuickViewModel,
    attraction: CurrencyAttraction
) {
    val height = (minSize + sizeDiff * attraction.attractionRatio).dp
    val fontSize = (minFontSize + fontSizeDiff * attraction.attractionRatio).sp

    Box(
        modifier = Modifier
            .tagCloudItemFade()
            .tagCloudItemScaleDown()
            .width(height)
            .height(height)
            .padding(10.dp)
            .clip(RoundedCornerShape(10))
            .background(Color.LightGray)
            .clickable { viewModel.onCurrencySelected(attraction.code) },
        contentAlignment = Alignment.Center
    ) {
        Text(text = attraction.code, fontSize = fontSize)
    }
}

@Composable
private fun GridQuickItem(
    viewModel: QuickViewModel,
    attraction: CurrencyAttraction
) {
    val height = (minSize + sizeDiff * attraction.attractionRatio).dp
    val fontSize = (minFontSize + fontSizeDiff * attraction.attractionRatio).sp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(10.dp)
            .clip(RoundedCornerShape(10))
            .background(Color.LightGray)
            .clickable {
                viewModel.onCurrencySelected(attraction.code)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = attraction.code, fontSize = fontSize)
    }
}

@Composable
private fun ListQuickItem(
    viewModel: QuickViewModel,
    attraction: CurrencyAttraction
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = { viewModel.onCurrencySelected(attraction.code) })
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(2.dp))
        Box(Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = attraction.code,
                fontSize = 20.sp
            )
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = attraction.name.name,
            )
        }
        Divider()
    }
}

private val showAsStates = listOf(
    QuickScreenShowAs.TAG_CLOUD to R.string.show_as_tag_cloud,
    QuickScreenShowAs.GRID to R.string.show_as_grid,
    QuickScreenShowAs.LIST to R.string.show_as_list,
)

@Composable
private fun ShowAsToggle(
    modifier: Modifier,
    viewModel: QuickViewModel,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        elevation = 4.dp,
        modifier = modifier
            .padding(vertical = 10.dp)
            .wrapContentSize()
    ) {
        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(24.dp))
                .background(Color.LightGray)
        ) {
            showAsStates.forEach { (showAs, id) ->
                Text(
                    text = stringResource(id),
                    color = Color.White,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(24.dp))
                        .clickable {
                            viewModel.apply {
                                viewModel.showAs = showAs
                                viewModelScope.launch {
                                    prefs.set(
                                        PreferenceKey.QuickScreenShowAsKey,
                                        showAs.ordinal
                                    )
                                }
                            }
                        }
                        .background(
                            if (showAs == viewModel.showAs) {
                                MaterialTheme.colors.secondary
                            } else {
                                Color.LightGray
                            }
                        )
                        .padding(
                            vertical = 12.dp,
                            horizontal = 16.dp,
                        ),
                )
            }
        }
    }
}

@Composable
private fun SortDialog(
    viewModel: QuickViewModel,
) {
    if (!viewModel.sortDialogVisible)
        return

    Dialog(onDismissRequest = { viewModel.sortDialogVisible = false }) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Sort by", style = MaterialTheme.typography.h6)
                SortItem(
                    R.string.sorted_by_used_count,
                    selected = viewModel.sortedBy == QuickScreenSortedBy.USED_COUNT
                ) {
                    viewModel.onSortedByPick(QuickScreenSortedBy.USED_COUNT)
                    viewModel.sortDialogVisible = false
                }
                SortItem(
                    R.string.sorted_by_used_time,
                    selected = viewModel.sortedBy == QuickScreenSortedBy.USED_TIME
                ) {
                    viewModel.onSortedByPick(QuickScreenSortedBy.USED_TIME)
                    viewModel.sortDialogVisible = false
                }
            }
        }
    }
}

@Composable
private fun SortItem(
    @StringRes
    item: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable { if (!selected) onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = item),
            style = MaterialTheme.typography.body2
        )
        RadioButton(selected = selected, onClick = { if (!selected) onClick() })
    }
}
