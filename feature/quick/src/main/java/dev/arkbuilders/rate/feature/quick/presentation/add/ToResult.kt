package dev.arkbuilders.rate.feature.quick.presentation.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.toBigDecimalArk
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.utils.ReorderHapticFeedback
import dev.arkbuilders.rate.core.presentation.utils.ReorderHapticFeedbackType
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun ToResult(
    code: CurrencyCode,
    amount: String,
    haptic: ReorderHapticFeedback,
    scope: ReorderableCollectionItemScope,
    onCurrencyRemove: () -> Unit,
    onCodeChange: () -> Unit,
) {
    Row(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        Box(
            modifier =
                with(scope) {
                    Modifier
                        .width(24.dp)
                        .height(44.dp)
                        .draggableHandle(
                            onDragStarted = {
                                haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
                            },
                            onDragStopped = {
                                haptic.performHapticFeedback(ReorderHapticFeedbackType.END)
                            },
                        )
                        .clearAndSetSemantics { }
                },
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                painter = painterResource(R.drawable.ic_drag),
                contentDescription = null,
                tint = ArkColor.NeutralGray500,
            )
        }

        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
                    .height(44.dp)
                    .border(
                        1.dp,
                        ArkColor.Border,
                        RoundedCornerShape(8.dp),
                    )
                    .background(Color.White, RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCodeChange() },
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
                    contentDescription = null,
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
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .clickable { onCurrencyRemove() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = stringResource(CoreRString.delete),
                tint = ArkColor.FGSecondary,
            )
        }
    }
}
