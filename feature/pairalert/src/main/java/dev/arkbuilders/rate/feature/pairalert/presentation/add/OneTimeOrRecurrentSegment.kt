package dev.arkbuilders.rate.feature.pairalert.presentation.add

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.presentation.CoreRString

@Composable
fun OneTimeOrRecurrent(
    byPrice: Boolean,
    oneTimeNotRecurrent: Boolean,
    onOneTimeChanged: (Boolean) -> Unit,
) {
    SegmentBtnRow(
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
