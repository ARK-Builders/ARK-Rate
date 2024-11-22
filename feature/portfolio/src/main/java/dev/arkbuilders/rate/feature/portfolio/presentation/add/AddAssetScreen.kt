package dev.arkbuilders.rate.feature.portfolio.presentation.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.AmountStr
import dev.arkbuilders.rate.core.presentation.AppSharedFlow
import dev.arkbuilders.rate.core.presentation.AppSharedFlowKey
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.core.presentation.ui.ArkBasicTextField
import dev.arkbuilders.rate.core.presentation.ui.DropDownWithIcon
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import dev.arkbuilders.rate.feature.portfolio.di.PortfolioComponentHolder
import dev.arkbuilders.rate.feature.portfolio.presentation.ui.PortfolioCreateDialog
import dev.arkbuilders.rate.feature.portfolio.presentation.ui.PortfolioSelectPopup
import dev.arkbuilders.rate.feature.search.presentation.destinations.SearchCurrencyScreenDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import dev.arkbuilders.rate.core.presentation.R as CoreR

@Destination
@Composable
fun AddAssetScreen(navigator: DestinationsNavigator) {
    val ctx = LocalContext.current
    val component =
        remember {
            PortfolioComponentHolder.provide(ctx)
        }
    val viewModel: AddAssetViewModel =
        viewModel(factory = component.addCurrencyVMFactory())

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            AddAssetSideEffect.NavigateBack -> navigator.popBackStack()
            is AddAssetSideEffect.NotifyAssetAdded -> {
                val added =
                    effect.amounts
                        .joinToString {
                            "${CurrUtils.prepareToDisplay(it.value)} ${it.code}"
                        }
                AppSharedFlow.ShowAddedSnackbarPortfolio.flow.emit(
                    NotifyAddedSnackbarVisuals(
                        ctx.getString(CoreRString.portfolio_snackbar_new_title),
                        ctx.getString(
                            CoreRString.portfolio_snackbar_new_desc,
                            added,
                        ),
                    ),
                )
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBarBack(
                title = stringResource(CoreRString.portfolio_add_new_assets),
                onBackClick = { navigator.popBackStack() },
            )
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            Content(
                state = state,
                onAssetValueChanged = viewModel::onAssetValueChange,
                onNewCurrencyClick = {
                    navigator.navigate(
                        SearchCurrencyScreenDestination(AppSharedFlowKey.AddAsset.toString()),
                    )
                },
                onAssetRemove = viewModel::onAssetRemove,
                onGroupSelect = viewModel::onGroupSelect,
                onCodeChange = {
                    navigator.navigate(
                        SearchCurrencyScreenDestination(AppSharedFlowKey.SetAssetCode.name, it),
                    )
                },
                onAddAsset = viewModel::onAddAsset,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun Content(
    state: AddAssetState = AddAssetState(emptyList(), group = "Hello"),
    onAssetValueChanged: (Int, String) -> Unit = { _, _ -> },
    onNewCurrencyClick: () -> Unit = {},
    onAssetRemove: (Int) -> Unit = {},
    onGroupSelect: (String) -> Unit = {},
    onCodeChange: (Int) -> Unit = {},
    onAddAsset: () -> Unit = {},
) {
    var showNewGroupDialog by remember { mutableStateOf(false) }
    var showGroupsPopup by remember { mutableStateOf(false) }
    var addGroupBtnWidth by remember { mutableStateOf(0) }

    if (showNewGroupDialog) {
        PortfolioCreateDialog(onDismiss = { showNewGroupDialog = false }) {
            onGroupSelect(it)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            Currencies(state, onAssetValueChanged, onAssetRemove, onCodeChange)
            Button(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ArkColor.Border),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = ArkColor.FGSecondary,
                    ),
                onClick = { onNewCurrencyClick() },
                contentPadding = PaddingValues(0.dp),
            ) {
                Icon(
                    modifier = Modifier.padding(start = 20.dp),
                    painter = painterResource(id = CoreRDrawable.ic_add),
                    contentDescription = "",
                )
                Text(
                    modifier =
                        Modifier.padding(
                            start = 8.dp,
                            top = 10.dp,
                            bottom = 10.dp,
                            end = 18.dp,
                        ),
                    text = stringResource(CoreRString.portfolio_new_asset),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
            DropDownWithIcon(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .onPlaced {
                            addGroupBtnWidth = it.size.width
                        },
                onClick = { showGroupsPopup = true },
                title =
                    state.group?.let { state.group }
                        ?: stringResource(CoreRString.portfolio_default_portfolio),
                icon = painterResource(id = CoreR.drawable.ic_group),
            )
            if (showGroupsPopup) {
                Box(
                    modifier =
                        Modifier.padding(
                            start = 16.dp,
                            top = 4.dp,
                        ),
                ) {
                    Popup(
                        offset = IntOffset(0, 0),
                        properties = PopupProperties(),
                        onDismissRequest = { showGroupsPopup = false },
                    ) {
                        PortfolioSelectPopup(
                            portfolios = state.availableGroups,
                            widthPx = addGroupBtnWidth,
                            onGroupSelect = { onGroupSelect(it) },
                            onNewGroupClick = { showNewGroupDialog = true },
                            onDismiss = { showGroupsPopup = false },
                        )
                    }
                }
            }
        }
        Column {
            HorizontalDivider(thickness = 1.dp, color = ArkColor.BorderSecondary)
            AppButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                onClick = {
                    onAddAsset()
                },
            ) {
                Text(text = stringResource(CoreRString.portfolio_add_new_assets))
            }
        }
    }
}

@Composable
private fun Currencies(
    state: AddAssetState,
    onAssetValueChanged: (Int, String) -> Unit,
    onAssetRemove: (Int) -> Unit,
    onCodeChange: (Int) -> Unit,
) {
    state.currencies.forEachIndexed { index, amount ->
        InputCurrency(
            index,
            amount,
            onAssetValueChanged,
            onAssetRemove,
            onCodeChange,
        )
    }
}

@Composable
fun InputCurrency(
    pos: Int,
    amount: AmountStr,
    onAssetValueChanged: (Int, String) -> Unit,
    onAssetRemove: (Int) -> Unit,
    onCodeChange: (Int) -> Unit,
) {
    Row(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .height(44.dp)
                    .border(
                        1.dp,
                        ArkColor.Border,
                        RoundedCornerShape(8.dp),
                    )
                    .clip(RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCodeChange(pos) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(start = 14.dp),
                    text = amount.code,
                    fontSize = 16.sp,
                    color = ArkColor.TextSecondary,
                )
                Icon(
                    modifier = Modifier.padding(start = 9.dp, end = 5.dp),
                    painter = painterResource(CoreRDrawable.ic_chevron),
                    contentDescription = "",
                    tint = ArkColor.FGQuinary,
                )
            }
            ArkBasicTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                value = amount.value,
                onValueChange = { onAssetValueChanged(pos, it) },
                textStyle =
                    TextStyle.Default.copy(
                        color = ArkColor.TextPrimary,
                        fontSize = 16.sp,
                    ),
                keyboardOptions =
                    KeyboardOptions.Default
                        .copy(keyboardType = KeyboardType.Number),
                placeholder = {
                    Text(
                        text = stringResource(CoreRString.input_value),
                        color = ArkColor.TextPlaceHolder,
                        fontSize = 16.sp,
                    )
                },
            )
        }

        Box(
            modifier =
                Modifier
                    .padding(start = 16.dp)
                    .size(44.dp)
                    .border(
                        1.dp,
                        ArkColor.Border,
                        RoundedCornerShape(8.dp),
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAssetRemove(pos) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = CoreR.drawable.ic_delete),
                contentDescription = "",
                tint = ArkColor.FGSecondary,
            )
        }
    }
}
