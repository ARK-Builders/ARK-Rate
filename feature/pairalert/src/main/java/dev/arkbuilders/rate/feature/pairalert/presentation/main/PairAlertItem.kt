package dev.arkbuilders.rate.feature.pairalert.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.CurrIcon
import dev.arkbuilders.rate.core.presentation.utils.DateFormatUtils
import dev.arkbuilders.rate.feature.pairalert.di.PairAlertComponent
import dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert
import timber.log.Timber

@Composable
fun PairAlertItem(
    component: PairAlertComponent,
    pairAlert: PairAlert,
    oneTimeTriggered: Boolean,
    onClick: (PairAlert) -> Unit,
    onEnableToggle: (PairAlert, Boolean) -> Unit,
) {
    var currencyName by remember {
        mutableStateOf("")
    }
    val currencyRepo = component.currencyRepo()
    LaunchedEffect(Unit) {
        currencyName = currencyRepo.nameByCodeUnsafe(pairAlert.targetCode).name
    }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable {
                    onClick(pairAlert)
                }
                .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CurrIcon(modifier = Modifier.size(40.dp), code = pairAlert.targetCode)
        Column(
            modifier =
                Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text =
                    "$currencyName(${pairAlert.targetCode}) " +
                        if (pairAlert.oneTimeNotRecurrent) "(One-time)" else "",
                fontWeight = FontWeight.Medium,
                color = ArkColor.TextPrimary,
            )
            Text(
                text =
                    buildString {
                        append(
                            "${
                                if (pairAlert.above())
                                    stringResource(CoreRString.above_c)
                                else
                                    stringResource(CoreRString.below_c)
                            } ",
                        )
                        append("${CurrUtils.prepareToDisplay(pairAlert.targetPrice)} ")
                        append(pairAlert.baseCode)
                    },
                color = ArkColor.TextTertiary,
            )
            if (oneTimeTriggered) {
                val date = pairAlert.lastDateTriggered
                date
                    ?: Timber.e("Pair alert marked as triggered but lastDateTriggered is null")
                if (date != null) {
                    Text(
                        text =
                            stringResource(
                                CoreRString.alert_notified_on,
                                DateFormatUtils.notifiedOn(date),
                            ),
                        color = ArkColor.TextTertiary,
                    )
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = pairAlert.enabled,
            onCheckedChange = { onEnableToggle(pairAlert, it) },
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedBorderColor = ArkColor.Primary,
                    checkedTrackColor = ArkColor.Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = ArkColor.BGTertiary,
                    uncheckedBorderColor = ArkColor.BGTertiary,
                ),
        )
    }
}
