package dev.arkbuilders.rate.presentation.assets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.model.CurrencyAmount
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.AddCurrencyScreenDestination
import dev.arkbuilders.rate.presentation.destinations.AssetsScreenDestination
import dev.arkbuilders.rate.presentation.destinations.SummaryScreenDestination
import dev.arkbuilders.rate.utils.removeFractionalPartIfEmpty

@Destination
@Composable
fun AssetsScreen(navigator: DestinationsNavigator) {
    val viewModel: AssetsViewModel = viewModel(factory = DIManager.component.assetsVMFactory())

    Column(Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.currencyAmountList, key = { it.code }) { currencyAmount ->
                CurrencyEditItem(modifier = Modifier, currencyAmount, viewModel)
            }
            ItemAdd(viewModel, navigator)
        }
        Button(modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally), onClick = {
            navigator.navigate(SummaryScreenDestination())
        }) {
            Icon(painterResource(R.drawable.ic_list_alt), contentDescription = "")
            Text(modifier = Modifier.padding(start = 4.dp),
                 text = "Summary",
                 style = MaterialTheme.typography.h6)
        }
    }
}

@Composable
private fun CurrencyEditItem(
        modifier: Modifier,
        amount: CurrencyAmount,
        viewModel: AssetsViewModel,
                            ) {
    val code = amount.code
    var amountInput by remember {
        mutableStateOf(if (amount.amount == 0.0) ""
                       else amount.amount.removeFractionalPartIfEmpty())
    }
    val clearIcon = @Composable {
        IconButton(onClick = {
            amountInput = viewModel.onAmountChanged(amount, amountInput, "")
        }) {
            Icon(Icons.Default.Clear, contentDescription = "")
        }
    }
    Row(modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
        OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = amountInput,
                onValueChange = { newInput ->
                    amountInput = viewModel.onAmountChanged(amount, amountInput, newInput)
                },
                trailingIcon = clearIcon,
                label = { Text(code) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                maxLines = 1,
                         )
        IconButton(modifier = Modifier.padding(8.dp),
                   onClick = { viewModel.onCurrencyRemoved(code) }) {
            Icon(Icons.Filled.Delete, "Delete")
        }
    }
}

private fun LazyListScope.ItemAdd(
        viewModel: AssetsViewModel,
        navigator: DestinationsNavigator,
                                 ) {
    if (viewModel.initialized) {
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                FloatingActionButton(
                        modifier = Modifier.padding(10.dp),
                        onClick = {
                            navigator.navigate(AddCurrencyScreenDestination(fromScreen = AssetsScreenDestination.route))
                        },
                                    ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        }
    }
}
