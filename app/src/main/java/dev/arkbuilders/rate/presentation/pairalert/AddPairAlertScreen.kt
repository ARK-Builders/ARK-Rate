@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.presentation.pairalert

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.painterResource
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
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.SearchCurrencyScreenDestination
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
fun AddPairAlertScreen(
    pairAlertId: Long? = null,
    navigator: DestinationsNavigator,
) {
    val viewModel: AddPairAlertViewModel =
        viewModel(
            factory = DIManager.component.addPairAlertVMFactory().create(pairAlertId)
        )

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            AddPairAlertScreenEffect.NavigateBack -> navigator.popBackStack()
            is AddPairAlertScreenEffect.NotifyPairAdded -> {
                val pair = effect.pair
                val aboveOrBelow = if (pair.above()) "above" else "below"
                val visuals = NotifyAddedSnackbarVisuals(
                    title = "Alert for ${pair.targetCode} has been created",
                    description = "You’ll get notified when ${pair.targetCode} " +
                            "price is $aboveOrBelow ${CurrUtils.prepareToDisplay(pair.targetPrice)} ${pair.baseCode}"
                )
                AppSharedFlow.ShowAddedSnackbarPairAlert.flow.emit(visuals)
            }
        }
    }


    var showNewGroupDialog by remember { mutableStateOf(false) }
    var showGroupsPopup by remember { mutableStateOf(false) }
    var addGroupBtnWidth by remember { mutableStateOf(0) }

    if (showNewGroupDialog) {
        GroupCreateDialog(onDismiss = { showNewGroupDialog = false }) {
            viewModel.onGroupSelect(it)
        }
    }

    Column {
        Column(modifier = Modifier.weight(1f)) {
            AppTopBarBack(title = "Add new alert", navigator = navigator)
            HorizontalDivider(thickness = 1.dp, color = ArkColor.BorderSecondary)
            PriceOrPercent(state, viewModel::onPriceOrPercentChanged)
            EditCondition(state, viewModel, navigator)
            OneTimeOrRecurrent(
                state.priceOrPercent.isLeft(),
                state.oneTimeNotRecurrent,
                viewModel::onOneTimeChanged
            )
            DropDownWithIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                    .onPlaced {
                        addGroupBtnWidth = it.size.width
                    },
                onClick = { showGroupsPopup = !showGroupsPopup },
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
                            onGroupSelect = { viewModel.onGroupSelect(it) },
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
                onClick = { viewModel.onSaveClick() },
                shape = RoundedCornerShape(8.dp),
                enabled = state.finishEnabled
            ) {
                Text(text = "Create Alert")
            }
        }
    }
}

@Composable
private fun PriceOrPercent(
    state: AddPairAlertScreenState,
    onPriceOrPercentChanged: (Boolean) -> Unit
) {
    SegmentBtnBg(
        modifier = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp
        )
    ) {
        SegmentBtn(
            modifier = Modifier
                .padding(6.dp)
                .weight(1f),
            title = "By price",
            enabled = state.priceOrPercent.isLeft()
        ) {
            onPriceOrPercentChanged(true)
        }
        SegmentBtn(
            modifier = Modifier
                .padding(6.dp)
                .weight(1f),
            title = "By percent",
            enabled = state.priceOrPercent.isRight()
        ) {
            onPriceOrPercentChanged(false)
        }
    }
}

@Composable
private fun SegmentBtnBg(
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .background(ArkColor.BGSecondaryAlt)
            .border(1.dp, ArkColor.BorderSecondary, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        content()
    }
}

@Composable
private fun SegmentBtn(
    modifier: Modifier,
    title: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 1.dp else 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color.White else ArkColor.BGSecondaryAlt,
        ),
        onClick = {
            onClick()
        },
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 10.dp),
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) ArkColor.TextBrandSecondary else ArkColor.TextTertiary
        )
    }
}

@Composable
private fun DropDownBtn(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(36.dp)
            .border(
                1.dp,
                ArkColor.Border,
                RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = title,
            fontWeight = FontWeight.SemiBold,
            color = ArkColor.FGSecondary
        )
        Icon(
            modifier = Modifier.padding(start = 8.dp, end = 15.dp),
            painter = painterResource(id = R.drawable.ic_chevron),
            contentDescription = "",
            tint = ArkColor.FGSecondary
        )
    }
}

@Composable
fun DropDownWithIcon(
    modifier: Modifier,
    onClick: () -> Unit,
    title: String,
    icon: Painter
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .border(
                1.dp,
                ArkColor.Border,
                RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(start = 16.dp),
            painter = icon,
            contentDescription = "",
            tint = ArkColor.FGSecondary
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            text = title,
            fontSize = 16.sp,
            color = ArkColor.TextPlaceHolder
        )
        Icon(
            modifier = Modifier.padding(end = 20.dp),
            painter = painterResource(id = R.drawable.ic_chevron),
            contentDescription = "",
            tint = ArkColor.FGSecondary
        )
    }
}

@Composable
private fun EditCondition(
    state: AddPairAlertScreenState,
    viewModel: AddPairAlertViewModel,
    navigator: DestinationsNavigator
) {
    Column(
        modifier = Modifier.padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "When", color = ArkColor.TextTertiary)
            DropDownBtn(
                modifier = Modifier.padding(start = 8.dp),
                title = state.targetCode
            ) {
                navigator.navigate(SearchCurrencyScreenDestination(AppSharedFlowKey.AddPairAlertTarget.name))
            }
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "price is",
                color = ArkColor.TextTertiary
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .run {
                        if (state.oneTimeNotRecurrent && state.priceOrPercent.isLeft())
                            this
                        else
                            clickable { viewModel.onIncreaseToggle() }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(start = 8.dp),
                    painter = painterResource(id = if (state.aboveNotBelow) R.drawable.ic_pair_alert_inc else R.drawable.ic_pair_alert_dec),
                    contentDescription = "",
                    tint = if (state.aboveNotBelow) ArkColor.PairAlertInc else ArkColor.PairAlertDec
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = if (state.aboveNotBelow) "above" else "below",
                    color = if (state.aboveNotBelow) ArkColor.PairAlertInc else ArkColor.PairAlertDec
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (!state.oneTimeNotRecurrent) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "every ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary
                )
            }
            BasicTextField(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .align(Alignment.CenterVertically),
                value = state.priceOrPercent.fold(
                    ifLeft = { it },
                    ifRight = { it }
                ),
                onValueChange = { viewModel.onPriceOrPercentInputChanged(it) },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 36.sp,
                    color = ArkColor.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                keyboardOptions = KeyboardOptions.Default
                    .copy(keyboardType = KeyboardType.Number)
            )
            if (state.priceOrPercent.isLeft()) {
                Text(
                    modifier = Modifier.align(Alignment.Top),
                    text = CurrUtils.getSymbolOrCode(state.baseCode),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary
                )
            }
            if (state.priceOrPercent.isRight()) {
                Text(
                    modifier = Modifier.align(Alignment.Top),
                    text = "%",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ArkColor.TextPrimary
                )
            }
        }
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Current price = ${CurrUtils.prepareToDisplay(state.currentPrice)}",
                color = ArkColor.TextTertiary
            )
            DropDownBtn(
                modifier = Modifier.padding(start = 16.dp),
                title = state.baseCode
            ) {
                navigator.navigate(SearchCurrencyScreenDestination(AppSharedFlowKey.AddPairAlertBase.name))
            }
        }
    }
}

@Composable
private fun OneTimeOrRecurrent(
    byPrice: Boolean,
    oneTimeNotRecurrent: Boolean,
    onOneTimeChanged: (Boolean) -> Unit
) {
    SegmentBtnBg(
        modifier = Modifier.padding(
            top = 32.dp,
            start = 16.dp,
            end = 16.dp
        )
    ) {
        SegmentBtn(
            modifier = Modifier
                .padding(6.dp)
                .weight(1f),
            title = "One-Time",
            enabled = oneTimeNotRecurrent
        ) {
            onOneTimeChanged(true)
        }
        SegmentBtn(
            modifier = Modifier
                .padding(6.dp)
                .weight(1f),
            title = if (byPrice) "Every" else "Recurrent",
            enabled = !oneTimeNotRecurrent
        ) {
            onOneTimeChanged(false)
        }
    }
}