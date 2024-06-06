@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.portfolio.EditAssetViewModel
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import dev.arkbuilders.rate.presentation.shared.AppSharedFlowKey
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.presentation.ui.CurrIcon
import dev.arkbuilders.rate.presentation.ui.LoadingScreen
import kotlinx.coroutines.launch
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
    OutlinedTextField(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        value = input,
        onValueChange = { onInputChange(it) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "",
                tint = ArkColor.FGQuarterary
            )
        },
        shape = RoundedCornerShape(8.dp),
        placeholder = {
            Text(
                text = "Search",
                color = ArkColor.TextPlaceHolder,
            )
        }
    )
    HorizontalDivider(thickness = 1.dp, color = ArkColor.BorderSecondary)
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
                        CurItem(name) { onClick(it) }
                    }
                }
            } else {
                NotFound()
            }
        }

        else -> {
            LazyColumn {
                if (frequent.isNotEmpty()) {
                    item { Header(header = "Frequent currencies") }
                    items(frequent) { name ->
                        CurItem(name) { onClick(it) }
                    }
                }
                item { Header(header = "All currencies") }
                items(all) { name ->
                    CurItem(name) { onClick(it) }
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
            bottom = 13.dp
        ),
        text = header,
        fontWeight = FontWeight.Medium,
        color = ArkColor.TextTertiary
    )
}


@Composable
private fun CurItem(
    name: CurrencyName,
    onClick: (CurrencyName) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .clickable { onClick(name) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurrIcon(modifier = Modifier.size(40.dp), code = name.code)
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name.code,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary
                )
                Text(text = name.name, color = ArkColor.TextTertiary)
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = ArkColor.BorderSecondary
        )
    }
}

@Composable
private fun NotFound() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                modifier = Modifier.size(72.dp),
                painter = painterResource(R.drawable.ic_search_refraction),
                contentDescription = "",
                tint = ArkColor.Secondary
            )
            Text(
                text = "No result",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = ArkColor.TextPrimary
            )
        }
    }
}


