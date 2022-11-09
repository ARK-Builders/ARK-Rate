package space.taran.arkrate.presentation.assets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import space.taran.arkrate.data.CurrencyAmount
import space.taran.arkrate.di.DIManager
import space.taran.arkrate.presentation.Screen
import space.taran.arkrate.utils.removeFractionalPartIfEmpty

@Composable
fun AssetsScreen(navController: NavController) {
    val viewModel: AssetsViewModel =
        viewModel(factory = DIManager.component.assetsVMFactory())

    Box(Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                viewModel.currencyAmountList,
                key = { it.code }
            ) { currencyAmount ->
                CurrencyEditItem(
                    modifier = Modifier,
                    currencyAmount,
                    viewModel
                )
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp),
            onClick = { navController.navigate(Screen.AddCurrency.name) },
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp),
            onClick = { navController.navigate(Screen.Summary.name) },
        ) {
            Icon(Icons.Filled.List, contentDescription = "Summary")
        }
    }
}

@Composable
private fun CurrencyEditItem(
    modifier: Modifier,
    currencyAmount: CurrencyAmount,
    viewModel: AssetsViewModel,
) {
    val code = currencyAmount.code
    var amountInput by remember {
        mutableStateOf(
            if (currencyAmount.amount == 0.0) ""
            else currencyAmount.amount.removeFractionalPartIfEmpty()
        )
    }
    val clearIcon = @Composable {
        IconButton(onClick = {
            amountInput =
                viewModel.onAmountChanged(code, amountInput, "")
        }) {
            Icon(
                Icons.Default.Clear,
                contentDescription = ""
            )
        }
    }
    Row(modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = amountInput,
            onValueChange = { newInput ->
                amountInput = viewModel.onAmountChanged(
                    code,
                    amountInput,
                    newInput
                )
            },
            trailingIcon = clearIcon,
            label = { Text(code) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            maxLines = 1,
        )
        IconButton(
            modifier = Modifier.padding(8.dp),
            onClick = { viewModel.onCurrencyRemoved(code) }
        ) {
            Icon(Icons.Filled.Delete, "Delete")
        }
    }
}
