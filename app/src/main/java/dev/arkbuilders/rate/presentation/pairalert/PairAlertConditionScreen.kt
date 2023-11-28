package dev.arkbuilders.rate.presentation.pairalert

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.data.model.PairAlertCondition
import dev.arkbuilders.rate.presentation.destinations.AddCurrencyScreenDestination
import dev.arkbuilders.rate.presentation.destinations.PairAlertConditionScreenDestination
import dev.arkbuilders.rate.presentation.shared.SharedViewModel
import dev.arkbuilders.rate.presentation.utils.activityViewModel
import dev.arkbuilders.rate.utils.removeFractionalPartIfEmpty

@Destination
@Composable
fun PairAlertConditionScreen(navigator: DestinationsNavigator,
        viewModel: SharedViewModel = activityViewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.pairAlertConditions, key = { it.id }) { condition ->
                ConditionItem(navigator, condition, viewModel)
            }
            item {
                ConditionItem(navigator, viewModel.newCondition, viewModel)
            }
        }
    }
}

@Composable
private fun ConditionItem(navigator: DestinationsNavigator,
        condition: PairAlertCondition,
        viewModel: SharedViewModel) {
    var ratioInput by remember {
        mutableStateOf(if (condition.ratio == 0.0f) ""
                       else condition.ratio.removeFractionalPartIfEmpty())
    }

    Card(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
        ) {
        Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Fraction(navigator, condition)
            Button(modifier = Modifier.padding(horizontal = 8.dp).width(40.dp).wrapContentHeight(),
                   onClick = { viewModel.onConditionMoreLessChanged(condition) }) {
                Text(if (condition.moreNotLess) ">" else "<")
            }
            OutlinedTextField(modifier = Modifier.weight(1f).wrapContentHeight()
                .padding(start = 2.dp, end = 2.dp),
                              value = ratioInput,
                              onValueChange = { newInput ->
                                  ratioInput =
                                      viewModel.onRatioChanged(condition, ratioInput, newInput)
                              },
                              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            AddOrDeleteBtn(viewModel, condition)
        }
    }
}

@Composable
private fun Fraction(navigator: DestinationsNavigator, condition: PairAlertCondition) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(modifier = Modifier.wrapContentSize().padding(start = 2.dp, end = 2.dp), onClick = {
            navigator.navigate(AddCurrencyScreenDestination(fromScreen = PairAlertConditionScreenDestination.route,
                                                            numeratorNotDenominator = true,
                                                            pairAlertConditionId = condition.id))
        }) {
            Text(condition.numeratorCode)
        }
        Box(modifier = Modifier.height(2.dp).width(60.dp).background(Color.LightGray))
        Button(modifier = Modifier.wrapContentSize().padding(start = 2.dp, end = 2.dp), onClick = {
            navigator.navigate(AddCurrencyScreenDestination(fromScreen = PairAlertConditionScreenDestination.route,
                                                            numeratorNotDenominator = false,
                                                            pairAlertConditionId = condition.id))
        }) {
            Text(condition.denominatorCode)
        }
    }
}

@Composable
private fun AddOrDeleteBtn(viewModel: SharedViewModel, condition: PairAlertCondition) {
    val ctx = LocalContext.current
    if (viewModel.newCondition == condition) {
        IconButton(modifier = Modifier.padding(8.dp), onClick = {
            if (!condition.isCompleted()) {
                Toast.makeText(ctx, "Pair alert is not completed", Toast.LENGTH_SHORT).show()
                return@IconButton
            }
            viewModel.onNewConditionSave()
        }) {
            Icon(Icons.Filled.Add, "Add")
        }
    } else {
        IconButton(modifier = Modifier.padding(8.dp), onClick = {
            Toast.makeText(ctx,
                           "Remove pair ${condition.numeratorCode}/${condition.denominatorCode}",
                           Toast.LENGTH_SHORT).show()
            viewModel.onRemoveCondition(condition)
        }) {
            Icon(Icons.Filled.Delete, "Delete")
        }
    }
}