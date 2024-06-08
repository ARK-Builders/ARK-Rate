@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.presentation.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.ui.AppHorDiv
import dev.arkbuilders.rate.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.presentation.ui.CurrencyInfoItem
import dev.arkbuilders.rate.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.presentation.ui.NoResult
import dev.arkbuilders.rate.presentation.ui.SearchTextField
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination
@Composable
fun SearchCurrencyScreen(
    appSharedFlowKeyString: String,
    pos: Int? = null,
    navigator: DestinationsNavigator,
) {
    val viewModel: SearchViewModel = viewModel(
        factory = DIManager.component.searchVMFactory()
            .create(appSharedFlowKeyString, pos)
    )
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SearchScreenEffect.NavigateBack -> navigator.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            Column {
                AppTopBarBack(title = "Search a currency", navigator = navigator)
                HorizontalDivider(thickness = 1.dp, color = ArkColor.BorderSecondary)
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            when {
                state.initialized.not() -> {
                    LoadingScreen()
                }

                else -> {
                    Input(state.filter, viewModel::onInputChange)
                    Results(
                        filter = state.filter,
                        frequent = state.frequent,
                        all = state.all,
                        topResultsFiltered = state.topResultsFiltered,
                        onClick = viewModel::onClick
                    )
                }
            }
        }
    }
}

@Composable
private fun Input(input: String, onInputChange: (String) -> Unit) {
    SearchTextField(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        text = input,
        onValueChange = { onInputChange(it) },
    )
    AppHorDiv()
}

@Composable
private fun Results(
    filter: String,
    frequent: List<CurrencyName>,
    all: List<CurrencyName>,
    topResultsFiltered: List<CurrencyName>,
    onClick: (CurrencyName) -> Unit
) {
    when {
        filter.isNotEmpty() -> {
            if (topResultsFiltered.isNotEmpty()) {
                LazyColumn {
                    item { Header(header = "Top results") }
                    items(topResultsFiltered) { name ->
                        CurrencyInfoItem(name) { onClick(it) }
                    }
                }
            } else {
                NoResult()
            }
        }

        else -> {
            LazyColumn {
                if (frequent.isNotEmpty()) {
                    item { Header(header = "Frequent currencies") }
                    items(frequent) { name ->
                        CurrencyInfoItem(name) { onClick(it) }
                    }
                }
                item { Header(header = "All currencies") }
                items(all) { name ->
                    CurrencyInfoItem(name) { onClick(it) }
                }
            }
        }
    }
}

@Composable
private fun Header(header: String) {
    Text(
        modifier = Modifier.padding(
            start = 16.dp,
            top = 24.dp,
            end = 16.dp,
        ),
        text = header,
        fontWeight = FontWeight.Medium,
        color = ArkColor.TextTertiary
    )
    AppHorDiv16(modifier = Modifier.padding(top = 12.dp))
}


