package dev.arkbuilders.rate.presentation.addcurrency

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.AssetsScreenDestination
import dev.arkbuilders.rate.presentation.destinations.PairAlertConditionScreenDestination
import dev.arkbuilders.rate.presentation.shared.SharedViewModel
import dev.arkbuilders.rate.presentation.utils.activityViewModel

@Destination
@Composable
fun AddCurrencyScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel = activityViewModel(),
    fromScreen: String,
    numeratorNotDenominator: Boolean? = null,
    pairAlertConditionId: Long? = null
) {
    val viewModel: AddCurrencyViewModel =
        viewModel(factory = DIManager.component.addCurrencyVMFactory())
    var filter by remember { mutableStateOf("") }
    val filteredCurrencyNameList = viewModel.currencyNameList?.filter { (code, _) ->
        code.startsWith(filter.uppercase())
    } ?: emptyList()

    Column(Modifier.fillMaxSize()) {
        Row {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp),
                value = filter,
                onValueChange = { filter = it },
                label = {
                    Text("Search")
                }
            )
        }
        LazyColumn {
            items(filteredCurrencyNameList.sortedBy { it.code }) { currencyName ->
                CurrencyItem(
                    code = currencyName.code,
                    currency = currencyName.name,
                    onAdd = {
                        when (fromScreen) {
                            AssetsScreenDestination.route -> viewModel.addCurrency(currencyName.code)
                            PairAlertConditionScreenDestination.route ->
                                sharedViewModel.onAlertConditionCodePicked(
                                    currencyName.code,
                                    numeratorNotDenominator!!,
                                    pairAlertConditionId!!
                                )
                        }
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun CurrencyItem(code: String, currency: String, onAdd: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = { onAdd() })
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(2.dp))
        Box(Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = code,
                fontSize = 20.sp
            )
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = currency,
            )
        }
        Divider()
    }
}