@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.portfolio.presentation.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.CurrencyName
import dev.arkbuilders.rate.core.presentation.AppSharedFlowKey
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.core.presentation.ui.ArkLargeTextField
import dev.arkbuilders.rate.core.presentation.ui.InfoMarketCapitalizationDialog
import dev.arkbuilders.rate.core.presentation.ui.InfoValueOfCirculatingDialog
import dev.arkbuilders.rate.core.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.feature.portfolio.di.PortfolioComponentHolder
import dev.arkbuilders.rate.feature.search.presentation.destinations.SearchCurrencyScreenDestination
import org.orbitmvi.orbit.compose.collectAsState

@Destination
@Composable
fun EditAssetScreen(
    assetId: Long,
    navigator: DestinationsNavigator,
) {
    val ctx = LocalContext.current
    val component =
        remember {
            PortfolioComponentHolder.provide(ctx)
        }

    val viewModel: EditAssetViewModel =
        viewModel(
            factory = component.editAssetVMFactory().create(assetId),
        )
    val state by viewModel.collectAsState()

    Scaffold(
        topBar = {
            AppTopBarBack(
                title = stringResource(CoreRString.asset_detail),
                onBackClick = { navigator.popBackStack() },
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

@Composable
private fun Content(
    navigator: DestinationsNavigator,
    name: CurrencyName,
    value: String,
    onValueChange: (String) -> Unit,
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
        val title =
            if (name.name.isNotEmpty()) {
                "${name.name} (${name.code})"
            } else {
                name.code
            }
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = title,
            color = ArkColor.TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
        )
        AppHorDiv(modifier = Modifier.padding(top = 21.dp))
        Row(
            Modifier.padding(top = 32.dp),
            verticalAlignment = Alignment.Top,
        ) {
            ArkLargeTextField(
                modifier =
                    Modifier
                        .weight(1f, fill = false)
                        .align(Alignment.CenterVertically),
                value = value,
                onValueChange = { onValueChange(it) },
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
                    SearchCurrencyScreenDestination(AppSharedFlowKey.PickBaseCurrency.toString()),
                )
            },
            contentPadding = PaddingValues(2.dp),
        ) {
            Icon(
                painter = painterResource(id = CoreRDrawable.ic_edit),
                contentDescription = "",
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = stringResource(CoreRString.change_base_currency),
                fontWeight = FontWeight.SemiBold,
            )
        }

        AppHorDiv(modifier = Modifier.padding(top = 32.dp))
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(CoreRString.market_capitalization),
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
                    painter = painterResource(id = CoreRDrawable.ic_info),
                    contentDescription = "",
                    tint = ArkColor.Primary,
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(CoreRString.n_a),
            fontWeight = FontWeight.SemiBold,
            color = ArkColor.TextPrimary,
        )
        AppHorDiv(modifier = Modifier.padding(top = 24.dp))
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(CoreRString.value_of_circulating_currency),
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
                    painter = painterResource(id = CoreRDrawable.ic_info),
                    contentDescription = "",
                    tint = ArkColor.Primary,
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = stringResource(CoreRString.n_a),
            fontWeight = FontWeight.SemiBold,
            color = ArkColor.TextPrimary,
        )
    }
}
