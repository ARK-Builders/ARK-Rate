@file:OptIn(ExperimentalComposeUiApi::class)

package dev.arkbuilders.rate.presentation.quick

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.model.CurrencyAmount
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.presentation.destinations.AddCurrencyScreenDestination
import dev.arkbuilders.rate.presentation.destinations.QuickScreenDestination
import dev.arkbuilders.rate.presentation.shared.SharedViewModel
import dev.arkbuilders.rate.presentation.utils.activityViewModel
import dev.arkbuilders.rate.utils.removeFractionalPartIfEmpty

@Composable
fun QuickScreenInputAmount(
    navigator: DestinationsNavigator,
    viewModel: QuickViewModel,
    amount: CurrencyAmount,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val ctx = LocalContext.current

    var amountInput by remember {
        mutableStateOf(
            if (amount.amount == 0.0) ""
            else amount.amount.removeFractionalPartIfEmpty()
        )
    }
    BackHandler {
        viewModel.selectedCurrency = null
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.hasFocus) {
                        keyboardController?.show()
                    }
                },
            value = amountInput,
            onValueChange = { newInput ->
                amountInput = viewModel.onAmountChanged(
                    amountInput,
                    newInput
                )
            },
            label = { Text(viewModel.selectedCurrency!!.code) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            maxLines = 1,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 6.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Convert ${amount.code} to:",
                style = MaterialTheme.typography.h5
            )
            viewModel.quickConvertToCurrency.forEach {
                QuickConvertToCurrencyItem(it.code, viewModel)
            }
            IconButton(
                onClick = {
                    navigator.navigate(
                        AddCurrencyScreenDestination(
                            fromScreen = QuickScreenDestination.route,
                            quickScreenConvertTo = true
                        )
                    )
                }
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(R.drawable.ic_add),
                    tint = MaterialTheme.colors.secondary,
                    contentDescription = ""
                )
            }
        }
        Button(
            modifier = Modifier.padding(12.dp),
            onClick = {
                if (viewModel.quickConvertToCurrency.isEmpty()) {
                    Toast.makeText(
                        ctx,
                        "Add at least one currency to convert into",
                        Toast.LENGTH_SHORT
                    ).show()
                } else
                    viewModel.onExchange()
            }
        ) {
            Text(text = "Convert", fontSize = 20.sp)
        }
    }
}

@Composable
private fun QuickConvertToCurrencyItem(
    code: CurrencyCode,
    viewModel: QuickViewModel
) {
    Row(
        Modifier.padding(top = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = code, style = MaterialTheme.typography.h6)
        IconButton(onClick = { viewModel.onRemoveConvertToCurrency(code) }) {
            Icon(
                painterResource(R.drawable.ic_delete_outline),
                tint = MaterialTheme.colors.secondary,
                contentDescription = ""
            )
        }
    }
}