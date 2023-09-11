package dev.arkbuilders.rate.presentation.addcurrency

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.AssetsScreenDestination
import dev.arkbuilders.rate.presentation.destinations.PairAlertConditionScreenDestination
import dev.arkbuilders.rate.presentation.destinations.QuickScreenDestination
import dev.arkbuilders.rate.presentation.shared.SharedViewModel
import dev.arkbuilders.rate.presentation.utils.activityViewModel
import dev.arkbuilders.rate.presentation.utils.collectInLaunchedEffectWithLifecycle
import java.time.Instant
import java.util.Calendar

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
    val ctx = LocalContext.current
    var filter by remember { mutableStateOf("") }
    val filteredCurrencyNameList = viewModel.currencyNameList?.filter { (code, name) ->
        code.startsWith(filter, ignoreCase = true) ||
                name.contains(filter, ignoreCase = true)
    } ?: emptyList()


    viewModel.eventsFlow.collectInLaunchedEffectWithLifecycle { event ->
        when (event) {
            AddCurrencyEvent.NavigateBack -> navController.popBackStack()
            is AddCurrencyEvent.NotifyCurrencyAdded -> {
                Toast.makeText(
                    ctx,
                    ctx.getString(
                        R.string.currency_is_already_added_to_assets,
                        event.code
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    Column(Modifier.fillMaxSize()) {
        Row {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp),
                value = filter,
                onValueChange = { filter = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
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
                            AssetsScreenDestination.route -> viewModel.addCurrency(
                                currencyName.code
                            )
                            PairAlertConditionScreenDestination.route -> {
                                sharedViewModel.onAlertConditionCodePicked(
                                    currencyName.code,
                                    numeratorNotDenominator!!,
                                    pairAlertConditionId!!
                                )
                                navController.popBackStack()
                            }
                            QuickScreenDestination.route -> {
                                sharedViewModel.onQuickCurrencyPicked(currencyName.code)
                                navController.popBackStack()
                            }
                        }
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