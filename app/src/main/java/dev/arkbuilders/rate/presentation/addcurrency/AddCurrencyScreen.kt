package dev.arkbuilders.rate.presentation.addcurrency

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.painterResource
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
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.SearchCurrencyScreenDestination
import dev.arkbuilders.rate.presentation.pairalert.DropDownWithIcon
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import dev.arkbuilders.rate.presentation.shared.AppSharedFlowKey
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.presentation.ui.GroupCreateDialog
import dev.arkbuilders.rate.presentation.ui.GroupSelectPopup
import dev.arkbuilders.rate.presentation.ui.NotifyAddedSnackbarVisuals
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination
@Composable
fun AddCurrencyScreen(
    navigator: DestinationsNavigator,
) {
    val viewModel: AddCurrencyViewModel =
        viewModel(factory = DIManager.component.addCurrencyVMFactory())

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            AddCurrencySideEffect.NavigateBack -> navigator.popBackStack()
            is AddCurrencySideEffect.NotifyAssetAdded -> {
                val added = effect.amounts
                    .joinToString {
                        "${CurrUtils.prepareToDisplay(it.amount)} ${it.code}"
                    }
                AppSharedFlow.ShowAddedSnackbarPortfolio.flow.emit(
                    NotifyAddedSnackbarVisuals(
                        "New Asset has been added",
                        "You added $added to your asset"
                    )
                )
            }
        }
    }

    Content(
        state = state,
        navigator = navigator,
        onAmountChanged = viewModel::onAssetAmountChange,
        onNewCurrencyClick = {
          navigator.navigate(SearchCurrencyScreenDestination(AppSharedFlowKey.AddAsset.toString()))
        },
        onAssetRemove = viewModel::onAssetRemove,
        onGroupSelect = viewModel::onGroupSelect,
        onCodeChange = {
            navigator.navigate(
                SearchCurrencyScreenDestination(
                    AppSharedFlowKey.SetCurrencyAmount.name,
                    it
                )
            )
        },
        onAddAsset = viewModel::onAddAsset
    )
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun Content(
    state: AddCurrencyState = AddCurrencyState(emptyList(), group = "Hello"),
    navigator: DestinationsNavigator = EmptyDestinationsNavigator,
    onAmountChanged: (Int, String) -> Unit = { _, _ -> },
    onNewCurrencyClick: () -> Unit = {},
    onAssetRemove: (Int) -> Unit = {},
    onGroupSelect: (String) -> Unit = {},
    onCodeChange: (Int) -> Unit = {},
    onAddAsset: () -> Unit = {}
) {
    var showNewGroupDialog by remember { mutableStateOf(false) }
    var showGroupsPopup by remember { mutableStateOf(false) }
    var addGroupBtnWidth by remember { mutableStateOf(0) }

    if (showNewGroupDialog) {
        GroupCreateDialog(onDismiss = { showNewGroupDialog = false }) {
            onGroupSelect(it)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            AppTopBarBack(title = "Add new asset", navigator = navigator)
            HorizontalDivider(thickness = 1.dp, color = ArkColor.BorderSecondary)
            Currencies(state, onAmountChanged, onAssetRemove, onCodeChange)
            Button(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ArkColor.Border),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = ArkColor.FGSecondary,
                ),
                onClick = { onNewCurrencyClick() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "",
                )
                Text(
                    text = "New Currency",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            DropDownWithIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .onPlaced {
                        addGroupBtnWidth = it.size.width
                    },
                onClick = { showGroupsPopup = true },
                title = state.group?.let { state.group } ?: "Add group",
                icon = painterResource(id = R.drawable.ic_group)
            )
            if (showGroupsPopup) {
                Box(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 4.dp
                    )
                ) {
                    Popup(
                        offset = IntOffset(0, 0),
                        properties = PopupProperties(),
                        onDismissRequest = { showGroupsPopup = false }
                    ) {
                        GroupSelectPopup(
                            groups = state.availableGroups,
                            widthPx = addGroupBtnWidth,
                            onGroupSelect = { onGroupSelect(it) },
                            onNewGroupClick = { showNewGroupDialog = true },
                            onDismiss = { showGroupsPopup = false }
                        )
                    }
                }
            }
        }
        Column {
            HorizontalDivider(thickness = 1.dp, color = ArkColor.BorderSecondary)
            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                onClick = {
                    onAddAsset()
                }
            ) {
                Text(text = "Add New Asset")
            }
        }
    }
}

@Composable
private fun Currencies(
    state: AddCurrencyState,
    onAmountChanged: (Int, String) -> Unit,
    onAssetRemove: (Int) -> Unit,
    onCodeChange: (Int) -> Unit,
) {
    state.currencies.forEachIndexed { index, amount ->
        InputCurrency(index, amount, onAmountChanged, onAssetRemove, onCodeChange)
    }
}

@Composable
fun InputCurrency(
    pos: Int,
    amount: Pair<CurrencyCode, String>,
    onAmountChanged: (Int, String) -> Unit,
    onAssetRemove: (Int) -> Unit,
    onCodeChange: (Int) -> Unit
) {
    Row(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .border(
                    1.dp,
                    ArkColor.Border,
                    RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable { onCodeChange(pos) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 14.dp),
                    text = amount.first,
                    fontSize = 16.sp,
                    color = ArkColor.TextSecondary
                )
                Icon(
                    modifier = Modifier.padding(start = 9.dp),
                    painter = painterResource(R.drawable.ic_chevron),
                    contentDescription = "",
                    tint = ArkColor.FGQuinary
                )
            }
            BasicTextField(
                modifier = Modifier.padding(start = 12.dp),
                value = amount.second,
                onValueChange = { onAmountChanged(pos, it) },
                textStyle = TextStyle.Default.copy(
                    color = ArkColor.TextPrimary,
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions.Default
                    .copy(keyboardType = KeyboardType.Number)
            )
        }

        IconButton(modifier = Modifier
            .padding(start = 16.dp)
            .size(44.dp)
            .border(
                1.dp,
                ArkColor.Border,
                RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp)),
            onClick = { onAssetRemove(pos) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "",
                tint = ArkColor.FGSecondary
            )
        }
    }
}
