@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.presentation.quick

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.presentation.theme.ArkColor
import kotlin.math.abs

@Composable
fun QuickOptionsBottomSheet(
    pair: QuickPair,
    onPin: (QuickPair) -> Unit,
    onUnpin: (QuickPair) -> Unit,
    onEdit: (QuickPair) -> Unit,
    onReuse: (QuickPair) -> Unit,
    onDelete: (QuickPair) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        modifier = Modifier,
        onDismissRequest = { onDismiss() },
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    ) {
        Content(
            pair = pair,
            onPin = onPin,
            onUnpin = onUnpin,
            onEdit = onEdit,
            onReuse = onReuse,
            onDelete = onDelete,
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun Content(
    pair: QuickPair,
    onPin: (QuickPair) -> Unit,
    onUnpin: (QuickPair) -> Unit,
    onEdit: (QuickPair) -> Unit,
    onReuse: (QuickPair) -> Unit,
    onDelete: (QuickPair) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(modifier = Modifier.verticalScrollDisabled()) {
        Text(
            modifier =
                Modifier
                    .padding(start = 16.dp, top = 24.dp)
                    .align(Alignment.TopStart),
            text = stringResource(R.string.options),
            color = ArkColor.TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )

        IconButton(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp),
            onClick = { onDismiss() },
        ) {
            Icon(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "",
                tint = ArkColor.FGQuinary,
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 76.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismiss()
                    if (pair.isPinned())
                        onUnpin(pair)
                    else
                        onPin(pair)
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ArkColor.Border),
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.ic_pin),
                    contentDescription = "",
                    tint = ArkColor.TextSecondary,
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text =
                        if (pair.isPinned())
                            stringResource(R.string.unpin)
                        else
                            stringResource(R.string.pin),
                    color = ArkColor.TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismiss()
                    onEdit(pair)
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ArkColor.Border),
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "",
                    tint = ArkColor.TextSecondary,
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = stringResource(R.string.edit),
                    color = ArkColor.TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismiss()
                    onReuse(pair)
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ArkColor.Border),
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.ic_reuse),
                    contentDescription = "",
                    tint = ArkColor.TextSecondary,
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = stringResource(R.string.re_use),
                    color = ArkColor.TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismiss()
                    onDelete(pair)
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ArkColor.BorderError),
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "",
                    tint = ArkColor.TextError,
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = stringResource(R.string.delete),
                    color = ArkColor.TextError,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

fun Modifier.verticalScrollDisabled() =
    then(
        pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach {
                        val offset = it.positionChange()
                        if (abs(offset.y) > 0f) {
                            it.consume()
                        }
                    }
                }
            }
        },
    )
