@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.quick.presentation.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.toBigDecimalArk
import dev.arkbuilders.rate.core.presentation.AppSharedFlow
import dev.arkbuilders.rate.core.presentation.AppSharedFlowKey
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.core.presentation.ui.ArkBasicTextField
import dev.arkbuilders.rate.core.presentation.ui.DropDownWithIcon
import dev.arkbuilders.rate.core.presentation.ui.GroupCreateDialog
import dev.arkbuilders.rate.core.presentation.ui.GroupSelectPopup
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import dev.arkbuilders.rate.feature.quick.di.QuickComponentHolder
import dev.arkbuilders.rate.feature.search.presentation.destinations.SearchCurrencyScreenDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
@Destination
fun AddQuickScreen(
    quickPairId: Long? = null,
    newCode: CurrencyCode? = null,
    reuseNotEdit: Boolean = true,
    groupId: Long? = null,
    navigator: DestinationsNavigator,
) {
    val ctx = LocalContext.current
    val quickComponent =
        remember {
            QuickComponentHolder.provide(ctx)
        }
    val viewModel: AddQuickViewModel =
        viewModel(
            factory =
                quickComponent.addQuickVMFactory()
                    .create(quickPairId, newCode, reuseNotEdit, groupId),
        )

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            AddQuickScreenEffect.NavigateBack -> navigator.popBackStack()
            is AddQuickScreenEffect.NotifyPairAdded -> {
                val added =
                    ctx.getString(
                        R.string.quick_snackbar_new_added_to,
                        effect.pair.from,
                        effect.pair.to.joinToString { it.code },
                    )
                AppSharedFlow.ShowAddedSnackbarQuick.flow.emit(
                    NotifyAddedSnackbarVisuals(
                        title = ctx.getString(R.string.quick_snackbar_new_title),
                        description =
                            ctx.getString(
                                R.string.quick_snackbar_new_desc,
                                added,
                            ),
                    ),
                )
            }

            is AddQuickScreenEffect.NavigateSearchAdd ->
                navigator.navigate(
                    SearchCurrencyScreenDestination(
                        appSharedFlowKeyString = AppSharedFlowKey.AddQuickCode.toString(),
                        prohibitedCodes = effect.prohibitedCodes.toTypedArray(),
                    ),
                )

            is AddQuickScreenEffect.NavigateSearchSet ->
                navigator.navigate(
                    SearchCurrencyScreenDestination(
                        appSharedFlowKeyString = AppSharedFlowKey.SetQuickCode.toString(),
                        pos = effect.index,
                        prohibitedCodes = effect.prohibitedCodes.toTypedArray(),
                    ),
                )
        }
    }
    Scaffold(
        topBar = {
            val title =
                if (reuseNotEdit)
                    R.string.quick_add_new_calculation
                else
                    R.string.quick_edit_pair
            AppTopBarBack(
                title = stringResource(title),
                onBackClick = { navigator.popBackStack() },
            )
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            Content(
                state = state,
                onAmountChanged = viewModel::onAssetAmountChange,
                onNewCurrencyClick = viewModel::onAddCode,
                onCurrencyRemove = viewModel::onCurrencyRemove,
                onGroupSelect = viewModel::onGroupSelect,
                onGroupCreate = viewModel::onGroupCreate,
                onCodeChange = viewModel::onSetCode,
                onAddAsset = viewModel::onAddQuickPair,
            )
        }
    }
}

@Composable
private fun Content(
    state: AddQuickScreenState,
    onAmountChanged: (String) -> Unit,
    onNewCurrencyClick: () -> Unit,
    onCurrencyRemove: (Int) -> Unit,
    onGroupSelect: (Group) -> Unit,
    onGroupCreate: (String) -> Unit,
    onCodeChange: (Int) -> Unit,
    onAddAsset: () -> Unit,
) {
    var showNewGroupDialog by remember { mutableStateOf(false) }
    var showGroupsPopup by remember { mutableStateOf(false) }
    var addGroupBtnWidth by remember { mutableStateOf(0) }

    if (showNewGroupDialog) {
        GroupCreateDialog(onDismiss = { showNewGroupDialog = false }) {
            onGroupCreate(it)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            Currencies(state, onAmountChanged, onCurrencyRemove, onCodeChange)
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
                    painter = painterResource(id = R.drawable.ic_add),
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
                    text = stringResource(R.string.new_currency),
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
                    state.group.name
                        ?: stringResource(R.string.add_group),
                icon = painterResource(id = R.drawable.ic_group),
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
                        GroupSelectPopup(
                            groups = state.availableGroups,
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
                enabled = state.finishEnabled,
            ) {
                Text(text = stringResource(R.string.save))
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
        color = ArkColor.TextSecondary,
    )
    FromInput(
        index = 0,
        code = from.code,
        amount = from.value,
        onAmountChanged = onAmountChanged,
        onCodeChange = onCodeChange,
    )
    AppHorDiv16(modifier = Modifier.padding(top = 16.dp))
    Text(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp),
        text = "To",
        fontWeight = FontWeight.Medium,
        color = ArkColor.TextSecondary,
    )
    state.currencies.forEachIndexed { index, amountStr ->
        if (index == 0)
            return@forEachIndexed

        ToResult(
            index = index,
            code = amountStr.code,
            amount = amountStr.value,
            onCurrencyRemove = onCurrencyRemove,
            onCodeChange = onCodeChange,
        )
    }
}

@Composable
private fun FromInput(
    index: Int,
    code: CurrencyCode,
    amount: String,
    onAmountChanged: (String) -> Unit,
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
                        .clickable { onCodeChange(index) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(start = 14.dp),
                    text = code,
                    fontSize = 16.sp,
                    color = ArkColor.TextSecondary,
                )
                Icon(
                    modifier = Modifier.padding(start = 9.dp, end = 5.dp),
                    painter = painterResource(R.drawable.ic_chevron),
                    contentDescription = "",
                    tint = ArkColor.FGQuinary,
                )
            }
            ArkBasicTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                value = amount,
                onValueChange = { onAmountChanged(it) },
                keyboardOptions =
                    KeyboardOptions.Default
                        .copy(keyboardType = KeyboardType.Number),
                textStyle =
                    TextStyle.Default.copy(
                        color = ArkColor.TextPrimary,
                        fontSize = 16.sp,
                    ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.input_value),
                        color = ArkColor.TextPlaceHolder,
                        fontSize = 16.sp,
                    )
                },
            )
        }
    }
}

@Composable
private fun ToResult(
    index: Int,
    code: CurrencyCode,
    amount: String,
    onCurrencyRemove: (Int) -> Unit,
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
                        .clickable { onCodeChange(index) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(start = 14.dp),
                    text = code,
                    fontSize = 16.sp,
                    color = ArkColor.TextSecondary,
                )
                Icon(
                    modifier = Modifier.padding(start = 9.dp, end = 5.dp),
                    painter = painterResource(R.drawable.ic_chevron),
                    contentDescription = "",
                    tint = ArkColor.FGQuinary,
                )
            }
            if (amount == "") {
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = stringResource(R.string.result),
                    color = ArkColor.TextPlaceHolder,
                    fontSize = 16.sp,
                )
            } else {
                Text(
                    modifier =
                        Modifier
                            .padding(start = 12.dp)
                            .horizontalScroll(rememberScrollState()),
                    text = CurrUtils.prepareToDisplay(amount.toBigDecimalArk()),
                    color = ArkColor.TextPrimary,
                    fontSize = 16.sp,
                )
            }
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
                    .clickable { onCurrencyRemove(index) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "",
                tint = ArkColor.FGSecondary,
            )
        }
    }
}
