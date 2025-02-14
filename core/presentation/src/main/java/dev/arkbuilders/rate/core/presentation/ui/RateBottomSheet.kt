@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun RateBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    ) {
        content()
    }
}

@Composable
fun BoxScope.RateBottomSheetTitle(
    title: String,
    onDismiss: () -> Unit,
) {
    Text(
        modifier =
            Modifier
                .padding(start = 16.dp, top = 24.dp)
                .align(Alignment.TopStart),
        text = title,
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
}
