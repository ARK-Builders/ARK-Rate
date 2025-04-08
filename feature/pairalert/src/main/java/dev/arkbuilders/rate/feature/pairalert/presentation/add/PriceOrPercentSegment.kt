package dev.arkbuilders.rate.feature.pairalert.presentation.add

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.presentation.CoreRString

@Composable
fun PriceOrPercent(
    state: AddPairAlertScreenState,
    onPriceOrPercentChanged: (Boolean) -> Unit,
) {
    SegmentBtnRow(
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
