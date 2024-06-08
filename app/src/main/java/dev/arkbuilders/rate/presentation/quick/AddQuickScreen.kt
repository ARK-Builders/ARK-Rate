@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.presentation.quick

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
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
import dev.arkbuilders.rate.data.toDoubleSafe
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.SearchCurrencyScreenDestination
import dev.arkbuilders.rate.presentation.pairalert.DropDownWithIcon
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import dev.arkbuilders.rate.presentation.shared.AppSharedFlowKey
import dev.arkbuilders.rate.presentation.theme.ArkColor
import dev.arkbuilders.rate.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.presentation.ui.GroupCreateDialog
import dev.arkbuilders.rate.presentation.ui.GroupSelectPopup
import dev.arkbuilders.rate.presentation.ui.NotifyAddedSnackbarVisuals
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
@Destination
fun AddQuickScreen(
    quickPairId: Long? = null,
    newCode: CurrencyCode? = null,
    navigator: DestinationsNavigator
) {
    val viewModel: AddQuickViewModel =
        viewModel(
            factory = DIManager.component.addQuickVMFactory()
                .create(quickPairId, newCode)
        )

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            AddQuickScreenEffect.NavigateBack -> navigator.popBackStack()
            is AddQuickScreenEffect.NotifyPairAdded -> {
                val added =
                    "${effect.pair.from} to ${effect.pair.to.joinToString { it }}"
                AppSharedFlow.ShowAddedSnackbarQuick.flow.emit(
                    NotifyAddedSnackbarVisuals(
                        title = "New pair has been created",
                        description = "You added $added pair "
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
            navigator.navigate(
                SearchCurrencyScreenDestination(
                    AppSharedFlowKey.AddQuickCode.toString()
                )
            )
        },
        onCurrencyRemove = viewModel::onCurrencyRemove,
        onGroupSelect = viewModel::onGroupSelect,
        onCodeChange = { index ->
            navigator.navigate(
                SearchCurrencyScreenDestination(
                    AppSharedFlowKey.SetQuickCode.name,
                    pos = index
                )
            )
        },
        onAddAsset = viewModel::onAddQuickPair
    )
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun Content(
    state: AddQuickScreenState = AddQuickScreenState(),
    navigator: DestinationsNavigator = EmptyDestinationsNavigator,
    onAmountChanged: (String) -> Unit = {},
    onNewCurrencyClick: () -> Unit = {},
    onCurrencyRemove: (Int) -> Unit = {},
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
            AppTopBarBack(title = "Add new pair", navigator = navigator)
            HorizontalDivider(thickness = 1.dp, color = ArkColor.BorderSecondary)
            Currencies(state, onAmountChanged, onCurrencyRemove, onCodeChange)
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
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    onAddAsset()
                },
                enabled = state.finishEnabled
            ) {
                Text(text = "Add Pair")
            }
        }
    }
}

@Composable
private fun Currencies(
    state: AddQuickScreenState,
    onAmountChanged: (String) -> Unit,
    onCurrencyRemove: (Int) -> Unit,
    onCodeChange: (Int) -> Unit,
) {
    val from = state.currencies.first()
    Text(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp),
        text = "From",
        fontWeight = FontWeight.Medium,
        color = ArkColor.TextSecondary
    )
    FromInput(
        index = 0,
        code = from.code,
        amount = from.value,
        onAmountChanged = onAmountChanged,
        onCodeChange = onCodeChange
    )
    AppHorDiv16(modifier = Modifier.padding(top = 16.dp))
    Text(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp),
        text = "To",
        fontWeight = FontWeight.Medium,
        color = ArkColor.TextSecondary
    )
    state.currencies.forEachIndexed { index, amountStr ->
        if (index == 0)
            return@forEachIndexed

        ToResult(
            index = index,
            code = amountStr.code,
            amount = amountStr.value,
            onCurrencyRemove = onCurrencyRemove,
            onCodeChange = onCodeChange
        )
    }
}

@Composable
private fun FromInput(
    index: Int,
    code: CurrencyCode,
    amount: String,
    onAmountChanged: (String) -> Unit,
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
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCodeChange(index) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 14.dp),
                    text = code,
                    fontSize = 16.sp,
                    color = ArkColor.TextSecondary
                )
                Icon(
                    modifier = Modifier.padding(start = 9.dp, end = 5.dp),
                    painter = painterResource(R.drawable.ic_chevron),
                    contentDescription = "",
                    tint = ArkColor.FGQuinary
                )
            }
            val interactionSource = remember { MutableInteractionSource() }
            BasicTextField(
                modifier = Modifier.padding(start = 12.dp),
                value = amount,
                onValueChange = { onAmountChanged(it) },
                textStyle = TextStyle.Default.copy(
                    color = ArkColor.TextPrimary,
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions.Default
                    .copy(keyboardType = KeyboardType.Number),
                interactionSource = interactionSource,
                singleLine = true
            ) { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = amount,
                    innerTextField = innerTextField,
                    placeholder = {
                        Text(
                            text = "Input the value",
                            color = ArkColor.TextPlaceHolder,
                            fontSize = 16.sp
                        )
                    },
                    contentPadding = PaddingValues(0.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource
                )
            }
        }
    }
}

@Composable
private fun ToResult(
    index: Int,
    code: CurrencyCode,
    amount: String,
    onCurrencyRemove: (Int) -> Unit,
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
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCodeChange(index) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 14.dp),
                    text = code,
                    fontSize = 16.sp,
                    color = ArkColor.TextSecondary
                )
                Icon(
                    modifier = Modifier.padding(start = 9.dp, end = 5.dp),
                    painter = painterResource(R.drawable.ic_chevron),
                    contentDescription = "",
                    tint = ArkColor.FGQuinary
                )
            }
            if (amount == "") {
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = "Result",
                    color = ArkColor.TextPlaceHolder,
                    fontSize = 16.sp
                )
            } else {
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = CurrUtils.prepareToDisplay(amount.toDoubleSafe()),
                    color = ArkColor.TextPrimary,
                    fontSize = 16.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(44.dp)
                .border(
                    1.dp,
                    ArkColor.Border,
                    RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable { onCurrencyRemove(index) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "",
                tint = ArkColor.FGSecondary
            )
        }
    }
}