@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.core.presentation.ui.group

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.utils.ReorderHapticFeedback
import dev.arkbuilders.rate.core.presentation.utils.ReorderHapticFeedbackType
import dev.arkbuilders.rate.core.presentation.utils.rememberReorderHapticFeedback
import sh.calvin.reorderable.ReorderableColumn
import sh.calvin.reorderable.ReorderableScope

data class EditGroupReorderSheetState(val groups: List<Group>)

@Composable
fun EditGroupReorderBottomSheet(
    sheetState: SheetState,
    state: EditGroupReorderSheetState,
    defaultName: String = stringResource(R.string.group_default_name),
    onSwap: (from: Int, to: Int) -> Unit,
    onOptionsClick: (Group) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        modifier = Modifier,
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    ) {
        Content(
            groups = state.groups,
            defaultName = defaultName,
            onSwap = onSwap,
            onOptionsClick = onOptionsClick,
            onDismiss = onDismiss,
        )
    }
}

@Composable
fun Content(
    groups: List<Group>,
    defaultName: String,
    onSwap: (from: Int, to: Int) -> Unit,
    onOptionsClick: (Group) -> Unit,
    onDismiss: () -> Unit,
) {
    val haptic = rememberReorderHapticFeedback()
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier =
                Modifier
                    .padding(start = 16.dp, top = 24.dp)
                    .align(Alignment.TopStart),
            text = stringResource(R.string.edit_group),
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
                painter = painterResource(id = CoreRDrawable.ic_close),
                contentDescription = "",
                tint = ArkColor.FGQuinary,
            )
        }
        ReorderableColumn(
            modifier = Modifier.padding(top = 72.dp),
            list = groups,
            onSettle = { fromIndex, toIndex ->
                onSwap(fromIndex, toIndex)
            },
            onMove = {
                haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
            },
        ) { scope, group, isDragging ->
            key(group) {
                GroupItem(haptic, this, group, defaultName, onOptionsClick)
            }
        }
    }
}

@Composable
private fun GroupItem(
    haptic: ReorderHapticFeedback,
    scope: ReorderableScope,
    group: Group,
    defaultName: String,
    onOptionsClick: (Group) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .height(62.dp)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(2.dp))
        Box(
            with(scope) {
                Modifier
                    .size(20.dp)
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
                painter = painterResource(CoreRDrawable.ic_drag),
                contentDescription = null,
            )
        }
        Spacer(Modifier.width(16.dp))
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(CoreRDrawable.ic_group),
            contentDescription = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = group.name ?: defaultName,
            color = ArkColor.TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.width(16.dp))
        IconButton(
            modifier = Modifier.size(40.dp),
            onClick = { onOptionsClick(group) },
        ) {
            Icon(
                painter = painterResource(CoreRDrawable.ic_dots),
                contentDescription = null,
            )
        }
    }
}
