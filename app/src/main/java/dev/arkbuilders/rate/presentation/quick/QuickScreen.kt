@file:OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

package dev.arkbuilders.rate.presentation.quick

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.data.CurrencyAmount
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
                        QuickItem(viewModel, it)
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
private fun TagCloudItemScope.QuickItem(
    viewModel: QuickViewModel,
    attraction: CurrencyAttraction
) {
    val height = (minSize + sizeDiff * attraction.attractionRatio).dp
    val fontSize = (minFontSize + fontSizeDiff * attraction.attractionRatio).sp

    val scope = rememberCoroutineScope()
    var handleClick = remember { true }
    val timerDelay = remember { 300L }
    var timerJob: Job? = remember { null }

    fun onPress() {
        timerJob?.cancel()
        handleClick = true
        timerJob = scope.launch {
            delay(timerDelay)
            if (isActive) {
                handleClick = false
            }
        }
    }

    Box(
        modifier = Modifier
            .tagCloudItemFade()
            .tagCloudItemScaleDown()
            .width(height)
            .height(height)
            .padding(10.dp)
            .clip(RoundedCornerShape(10))
            .background(Color.LightGray)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Press -> { onPress() }
                            PointerEventType.Release -> {
                                if (handleClick) {
                                    viewModel.onCurrencySelected(attraction.code)
                                }
                            }
                        }
                    }
                }
            }
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Text(text = attraction.code, fontSize = fontSize)
    }
}
