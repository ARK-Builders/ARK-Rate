@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.pairalert.presentation.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.presentation.AppSharedFlow
import dev.arkbuilders.rate.core.presentation.AppSharedFlowKey
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.core.presentation.ui.ArkLargeTextField
import dev.arkbuilders.rate.core.presentation.ui.DropDownWithIcon
import dev.arkbuilders.rate.core.presentation.ui.GroupCreateDialog
import dev.arkbuilders.rate.core.presentation.ui.GroupSelectPopup
import dev.arkbuilders.rate.core.presentation.ui.NotifyAddedSnackbarVisuals
import dev.arkbuilders.rate.feature.pairalert.di.PairAlertComponentHolder
import dev.arkbuilders.rate.feature.search.presentation.destinations.SearchCurrencyScreenDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import dev.arkbuilders.rate.core.presentation.R as CoreR

@Destination
@Composable
fun AddPairAlertScreen(
    pairAlertId: Long? = null,
    groupId: Long? = null,
    navigator: DestinationsNavigator,
) {
    val ctx = LocalContext.current
    val component =
        remember {
            PairAlertComponentHolder.provide(ctx)
        }
    val viewModel: AddPairAlertViewModel =
        viewModel(
            factory = component.addPairAlertVMFactory().create(pairAlertId, groupId),
        )

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            AddPairAlertScreenEffect.NavigateBack -> navigator.popBackStack()
            is AddPairAlertScreenEffect.NotifyPairAdded -> {
                val pair = effect.pair
                val aboveOrBelow =
                    if (pair.above())
                        ctx.getString(CoreRString.above)
                    else
                        ctx.getString(CoreRString.below)
                val visuals =
                    NotifyAddedSnackbarVisuals(
                        title =
                            ctx.getString(
                                CoreRString.alert_snackbar_new_title,
                                pair.targetCode,
                            ),
                        description =
                            ctx.getString(
                                CoreRString.alert_snackbar_new_desc,
                                pair.targetCode,
                                aboveOrBelow,
                                CurrUtils.prepareToDisplay(pair.targetPrice),
                                pair.baseCode,
                            ),
                    )
                AppSharedFlow.ShowAddedSnackbarPairAlert.flow.emit(visuals)
            }

            is AddPairAlertScreenEffect.NavigateSearchBase ->
                navigator.navigate(
                    SearchCurrencyScreenDestination(
                        appSharedFlowKeyString = AppSharedFlowKey.AddPairAlertBase.name,
                        prohibitedCodes = effect.prohibitedCodes.toTypedArray(),
                    ),
                )

            is AddPairAlertScreenEffect.NavigateSearchTarget ->
                navigator.navigate(
                    SearchCurrencyScreenDestination(
                        appSharedFlowKeyString = AppSharedFlowKey.AddPairAlertTarget.name,
                        prohibitedCodes = effect.prohibitedCodes.toTypedArray(),
                    ),
                )
        }
    }

    Scaffold(
        topBar = {
            AppTopBarBack(
                title =
                    if (state.editExisting)
                        stringResource(CoreRString.alert_edit_alert)
                    else
                        stringResource(CoreRString.add_new_alert),
                onBackClick = { navigator.popBackStack() },
            )
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            Content(
                state = state,
                navigateSearchBase = viewModel::onNavigateSearchBase,
                navigateSearchTarget = viewModel::onNavigateSearchTarget,
                onGroupSelect = viewModel::onGroupSelect,
                onGroupCreate = viewModel::onGroupCreate,
                onPriceOrPercentChanged = viewModel::onPriceOrPercentChanged,
                onOneTimeChanged = viewModel::onOneTimeChanged,
                onPriceOrPercentInputChanged = viewModel::onPriceOrPercentInputChanged,
                onIncreaseToggle = viewModel::onIncreaseToggle,
                onSaveClick = viewModel::onSaveClick,
            )
        }
    }
}

@Composable
private fun Content(
    state: AddPairAlertScreenState,
    navigateSearchBase: () -> Unit,
    navigateSearchTarget: () -> Unit,
    onGroupSelect: (Group) -> Unit,
    onGroupCreate: (String) -> Unit,
    onPriceOrPercentChanged: (Boolean) -> Unit,
    onOneTimeChanged: (Boolean) -> Unit,
    onPriceOrPercentInputChanged: (String) -> Unit,
    onIncreaseToggle: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val ctx = LocalContext.current
    var showNewGroupDialog by remember { mutableStateOf(false) }
    var showGroupsPopup by remember { mutableStateOf(false) }
    var addGroupBtnWidth by remember { mutableStateOf(0) }

    if (showNewGroupDialog) {
        GroupCreateDialog(
            validateGroupNameUseCase =
                PairAlertComponentHolder.provide(ctx)
                    .validateGroupNameUseCase(),
            onDismiss = { showNewGroupDialog = false },
        ) {
            onGroupCreate(it)
        }
    }

    Column {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            PriceOrPercent(state, onPriceOrPercentChanged)
            EditCondition(
                state = state,
                navigateSearchBase = navigateSearchBase,
                navigateSearchTarget = navigateSearchTarget,
                onPriceOrPercentInputChanged = onPriceOrPercentInputChanged,
                onIncreaseToggle = onIncreaseToggle,
            )
            OneTimeOrRecurrent(
                state.priceOrPercent.isLeft(),
                state.oneTimeNotRecurrent,
                onOneTimeChanged,
            )
            DropDownWithIcon(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        .onPlaced {
                            addGroupBtnWidth = it.size.width
                        },
                onClick = { showGroupsPopup = !showGroupsPopup },
                title = state.group.name,
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
                        GroupSelectPopup(
                            groups = state.availableGroups,
                            widthPx = addGroupBtnWidth,
                            onGroupSelect = onGroupSelect,
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
                onClick = onSaveClick,
                enabled = state.finishEnabled,
            ) {
                Text(
                    text =
                        if (state.editExisting)
                            stringResource(CoreRString.save)
                        else
                            stringResource(CoreRString.create_alert),
                )
            }
        }
    }
}

@Composable
private fun PriceOrPercent(
    state: AddPairAlertScreenState,
    onPriceOrPercentChanged: (Boolean) -> Unit,
) {
    SegmentBtnBg(
        modifier =
            Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
            ),
    ) {
        SegmentBtn(
            modifier =
                Modifier
                    .padding(6.dp)
                    .weight(1f),
            title = stringResource(CoreRString.by_price),
            enabled = state.priceOrPercent.isLeft(),
        ) {
            onPriceOrPercentChanged(true)
        }
        SegmentBtn(
            modifier =
                Modifier
                    .padding(6.dp)
                    .weight(1f),
            title = stringResource(CoreRString.by_percent),
            enabled = state.priceOrPercent.isRight(),
        ) {
            onPriceOrPercentChanged(false)
        }
    }
}

@Composable
private fun SegmentBtnBg(
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier =
            modifier
                .background(ArkColor.BGSecondaryAlt)
                .border(1.dp, ArkColor.BorderSecondary, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        content()
    }
}

@Composable
private fun SegmentBtn(
    modifier: Modifier,
    title: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 1.dp else 0.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = if (enabled) Color.White else ArkColor.BGSecondaryAlt,
            ),
        onClick = {
            onClick()
        },
    ) {
        Text(
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp),
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) ArkColor.TextBrandSecondary else ArkColor.TextTertiary,
        )
    }
}

@Composable
private fun DropDownBtn(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .height(36.dp)
                .border(
                    1.dp,
                    ArkColor.Border,
                    RoundedCornerShape(8.dp),
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = title,
            fontWeight = FontWeight.SemiBold,
            color = ArkColor.FGSecondary,
        )
        Icon(
            modifier = Modifier.padding(start = 8.dp, end = 15.dp),
            painter = painterResource(id = CoreRDrawable.ic_chevron),
            contentDescription = "",
            tint = ArkColor.FGSecondary,
        )
    }
}

@Composable
private fun EditCondition(
    state: AddPairAlertScreenState,
    navigateSearchBase: () -> Unit,
    navigateSearchTarget: () -> Unit,
    onPriceOrPercentInputChanged: (String) -> Unit,
    onIncreaseToggle: () -> Unit,
) {
    val ctx = LocalContext.current
    Column(
        modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(CoreRString.when_),
                color = ArkColor.TextTertiary,
            )
            DropDownBtn(
                modifier = Modifier.padding(start = 8.dp),
                title = state.targetCode,
            ) {
                navigateSearchTarget()
            }
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(CoreRString.price_is),
                color = ArkColor.TextTertiary,
            )
            Row(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .run {
                            if (state.oneTimeNotRecurrent && state.priceOrPercent.isLeft())
                                this
                            else
                                clickable { onIncreaseToggle() }
                        },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(start = 8.dp),
                    painter =
                        painterResource(
                            id =
                                if (state.aboveNotBelow)
                                    CoreRDrawable.ic_pair_alert_inc
                                else
                                    CoreRDrawable.ic_pair_alert_dec,
                        ),
                    contentDescription = "",
                    tint =
                        if (state.aboveNotBelow)
                            ArkColor.PairAlertInc
                        else
                            ArkColor.PairAlertDec,
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text =
                        if (state.aboveNotBelow)
                            ctx.getString(CoreRString.above)
                        else
                            ctx.getString(CoreRString.below),
                    color =
                        if (state.aboveNotBelow)
                            ArkColor.PairAlertInc
                        else
                            ArkColor.PairAlertDec,
                )
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (!state.oneTimeNotRecurrent) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(CoreRString.every),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary,
                )
            }
            ArkLargeTextField(
                modifier =
                    Modifier
                        .weight(1f, fill = false)
                        .align(Alignment.CenterVertically),
                value =
                    state.priceOrPercent.fold(
                        ifLeft = { it },
                        ifRight = { it },
                    ),
                onValueChange = { onPriceOrPercentInputChanged(it) },
            )
            if (state.priceOrPercent.isLeft()) {
                Text(
                    modifier = Modifier.align(Alignment.Top),
                    text = CurrUtils.getSymbolOrCode(state.baseCode),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary,
                )
            }
            if (state.priceOrPercent.isRight()) {
                Text(
                    modifier = Modifier.align(Alignment.Top),
                    text = "%",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ArkColor.TextPrimary,
                )
            }
        }
        Row(
            modifier =
                Modifier
                    .padding(top = 24.dp)
                    .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text =
                    stringResource(
                        CoreRString.alert_current_price,
                        CurrUtils.prepareToDisplay(state.currentPrice),
                    ),
                color = ArkColor.TextTertiary,
            )
            DropDownBtn(
                modifier = Modifier.padding(start = 16.dp),
                title = state.baseCode,
            ) {
                navigateSearchBase()
            }
        }
    }
}

@Composable
private fun OneTimeOrRecurrent(
    byPrice: Boolean,
    oneTimeNotRecurrent: Boolean,
    onOneTimeChanged: (Boolean) -> Unit,
) {
    SegmentBtnBg(
        modifier =
            Modifier.padding(
                top = 32.dp,
                start = 16.dp,
                end = 16.dp,
            ),
    ) {
        SegmentBtn(
            modifier =
                Modifier
                    .padding(6.dp)
                    .weight(1f),
            title = stringResource(CoreRString.one_time),
            enabled = oneTimeNotRecurrent,
        ) {
            onOneTimeChanged(true)
        }
        SegmentBtn(
            modifier =
                Modifier
                    .padding(6.dp)
                    .weight(1f),
            title =
                if (byPrice)
                    stringResource(CoreRString.every_c)
                else
                    stringResource(CoreRString.recurrent),
            enabled = !oneTimeNotRecurrent,
        ) {
            onOneTimeChanged(false)
        }
    }
}
