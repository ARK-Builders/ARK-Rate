package dev.arkbuilders.rate.presentation.portfolio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.presentation.destinations.SearchCurrencyScreenDestination
import dev.arkbuilders.rate.presentation.shared.AppSharedFlowKey
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.ui.AppHorDiv
import dev.arkbuilders.rate.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.presentation.ui.InfoMarketCapitalizationDialog
import dev.arkbuilders.rate.presentation.ui.InfoValueOfCirculatingDialog
import dev.arkbuilders.rate.presentation.ui.LoadingScreen
import org.orbitmvi.orbit.compose.collectAsState

@Destination
@Composable
fun EditAssetScreen(
    assetId: Long,
    navigator: DestinationsNavigator,
) {
    val viewModel: EditAssetViewModel =
        viewModel(
            factory = DIManager.component.editAssetVMFactory().create(assetId),
        )
    val state by viewModel.collectAsState()

    Scaffold(
        topBar = {
            AppTopBarBack(
                title = stringResource(R.string.asset_detail),
                navigator = navigator,
            )
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            if (state.initialized) {
                Content(
                    navigator = navigator,
                    name = state.name,
                    value = state.value,
                    onValueChange = viewModel::onValueChange,
                )
            } else {
                LoadingScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Content(
    navigator: DestinationsNavigator = EmptyDestinationsNavigator,
    name: CurrencyName = CurrencyName("USD", "United States dollar"),
    value: String = "1000.02",
    onValueChange: (String) -> Unit = {},
) {
    var showMarketCapitalizationDialog by remember { mutableStateOf(false) }
    var showValueOfCirculatingDialog by remember { mutableStateOf(false) }

    if (showMarketCapitalizationDialog) {
        InfoMarketCapitalizationDialog { showMarketCapitalizationDialog = false }
    }

    if (showValueOfCirculatingDialog) {
        InfoValueOfCirculatingDialog { showValueOfCirculatingDialog = false }
    }

    Column(
        modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
    ) {
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = "${name.name} (${name.code})",
            color = ArkColor.TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
        )
        AppHorDiv(modifier = Modifier.padding(top = 21.dp))
        Row(
            Modifier.padding(top = 32.dp),
            verticalAlignment = Alignment.Top,
        ) {
            BasicTextField(
                modifier =
                    Modifier
                        .width(IntrinsicSize.Min)
                        .align(Alignment.CenterVertically),
                value = value,
                onValueChange = { onValueChange(it) },
                textStyle =
                    LocalTextStyle.current.copy(
                        fontSize = 36.sp,
                        color = ArkColor.TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                keyboardOptions =
                    KeyboardOptions.Default
                        .copy(keyboardType = KeyboardType.Number),
            )
            Text(
                modifier =
                    Modifier
                        .padding(start = 2.dp, top = 2.dp)
                        .align(Alignment.Top),
                text = CurrUtils.getSymbolOrCode(name.code),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = ArkColor.TextPrimary,
            )
        }
        TextButton(
            modifier =
                Modifier
                    .padding(top = 16.dp)
                    .height(22.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = ArkColor.BrandUtility),
            onClick = {
                navigator.navigate(
                    SearchCurrencyScreenDestination(
                        AppSharedFlowKey.PickBaseCurrency.toString(),
                    ),
                )
            },
            contentPadding = PaddingValues(2.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = "",
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = stringResource(R.string.change_base_currency),
                fontWeight = FontWeight.SemiBold,
            )
        }

        AppHorDiv(modifier = Modifier.padding(top = 32.dp))
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.market_capitalization),
                fontWeight = FontWeight.Medium,
                color = ArkColor.TextTertiary,
            )
            IconButton(
                modifier =
                    Modifier
                        .padding(start = 4.dp)
                        .size(20.dp),
                onClick = { showMarketCapitalizationDialog = true },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "",
                    tint = ArkColor.Primary,
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(R.string.n_a),
            fontWeight = FontWeight.SemiBold,
            color = ArkColor.TextPrimary,
        )
        AppHorDiv(modifier = Modifier.padding(top = 24.dp))
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.value_of_circulating_currency),
                fontWeight = FontWeight.Medium,
                color = ArkColor.TextTertiary,
            )
            IconButton(
                modifier =
                    Modifier
                        .padding(start = 4.dp)
                        .size(20.dp),
                onClick = { showValueOfCirculatingDialog = true },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "",
                    tint = ArkColor.Primary,
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = stringResource(R.string.n_a),
            fontWeight = FontWeight.SemiBold,
            color = ArkColor.TextPrimary,
        )
    }
}
