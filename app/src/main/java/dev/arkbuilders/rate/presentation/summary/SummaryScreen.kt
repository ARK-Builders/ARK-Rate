package dev.arkbuilders.rate.presentation.summary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import dev.arkbuilders.rate.data.CurrencyAmount
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.SummaryScreenDestination
import java.math.RoundingMode
import java.text.DecimalFormat

private val format = DecimalFormat("0.######").apply {
    roundingMode = RoundingMode.HALF_DOWN
}

@Destination
@Composable
fun SummaryScreen(
    amount: CurrencyAmount? = null
) {
    val viewModel: SummaryViewModel =
        viewModel(
            factory = DIManager.component.summaryViewModelFactory().create(amount)
        )
    Box(Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.align(Alignment.Center)) {
            amount?.let {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Quick ${amount.code}", fontSize = 20.sp)
                    }
                }
            }
            item {
                TotalCard(viewModel)
            }
            item {
                ExchangeCard(viewModel)
            }
        }
    }
}

@Composable
private fun TotalCard(viewModel: SummaryViewModel) {
    val total by viewModel.total.collectAsState()
    total ?: return
    var visible by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 15.dp)
            .clickable {
                visible = !visible
            },
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("TOTAL", fontSize = 24.sp)
            Divider()
            AnimatedVisibility(visible) {
                Column {
                    total!!.forEach {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                        ) {
                            Text(
                                it.key,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                            Text(
                                format.format(it.value),
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExchangeCard(viewModel: SummaryViewModel) {
    val exchange by viewModel.exchange.collectAsState()
    exchange ?: return
    var visible by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 15.dp)
            .clickable {
                visible = !visible
            },
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("EXCHANGE", fontSize = 24.sp)
            Divider()
            AnimatedVisibility(visible) {
                Column {
                    exchange!!.forEach {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                        ) {
                            Text(
                                it.key,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                            Text(
                                it.value,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}