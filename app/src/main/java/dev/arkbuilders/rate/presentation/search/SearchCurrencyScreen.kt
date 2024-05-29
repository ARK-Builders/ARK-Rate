@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.model.CurrencyName
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import dev.arkbuilders.rate.presentation.shared.AppSharedFlowKey
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.presentation.ui.CurrIcon
import kotlinx.coroutines.launch

@Destination
@Composable
fun SearchCurrencyScreen(
    appSharedFlowKeyString: String,
    pos: Int? = null,
    navigator: DestinationsNavigator,
) {
    val appSharedFlowKey = AppSharedFlowKey.valueOf(appSharedFlowKeyString)
    val input = remember { mutableStateOf("") }
    Column {
        AppTopBarBack(title = "Search a currency", navigator = navigator)
        HorizontalDivider(thickness = 1.dp, color = ArkColor.BorderSecondary)
        Input(input)
        Results(input.value, appSharedFlowKey, pos, navigator)
    }
}

@Composable
private fun Input(inputState: MutableState<String>) {
    OutlinedTextField(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        value = inputState.value,
        onValueChange = { inputState.value = it },
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
    input: String,
    appSharedFlowKey: AppSharedFlowKey,
    pos: Int?,
    navigator: DestinationsNavigator
) {
    val allCurrencies = remember {
        mutableStateListOf<CurrencyName>()
    }
    LaunchedEffect(key1 = Unit) {
        allCurrencies.addAll(
            DIManager.component.generalCurrencyRepo().getCurrencyName()
        )
    }
    val filtered = allCurrencies
        .filter {
            it.name.contains(input, ignoreCase = true)
                    || it.code.contains(input, ignoreCase = true)
        }

    Text(
        modifier = Modifier.padding(
            start = 16.dp,
            top = 24.dp,
            end = 16.dp,
            bottom = 13.dp
        ),
        text = "Top results",
        fontWeight = FontWeight.Medium,
        color = ArkColor.TextTertiary
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = ArkColor.BorderSecondary
    )
    LazyColumn {
        items(filtered) {
            CurItem(it, appSharedFlowKey, pos, navigator)
        }
    }
}


@Composable
private fun CurItem(
    name: CurrencyName,
    appSharedFlowKey: AppSharedFlowKey,
    pos: Int?,
    navigator: DestinationsNavigator
) {
    val scope = rememberCoroutineScope()

    suspend fun emitResult() {
        val appFlow = AppSharedFlow.fromKey(appSharedFlowKey)
        when (appFlow) {
            AppSharedFlow.SetCurrencyAmount ->
                AppSharedFlow.SetCurrencyAmount.flow.emit(pos!! to name.code)

            AppSharedFlow.AddAsset -> AppSharedFlow.AddAsset.flow.emit(name.code)

            AppSharedFlow.AddPairAlertBase ->
                AppSharedFlow.AddPairAlertBase.flow.emit(name.code)

            AppSharedFlow.AddPairAlertTarget ->
                AppSharedFlow.AddPairAlertTarget.flow.emit(name.code)

            AppSharedFlow.AddQuick -> AppSharedFlow.AddQuick.flow.emit(pos!! to name.code)

            AppSharedFlow.PickBaseCurrency -> AppSharedFlow.PickBaseCurrency.flow.emit(
                name.code
            )

            else -> {}
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .clickable {
                    scope.launch {
                        emitResult()
                        navigator.popBackStack()
                    }
                },
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


