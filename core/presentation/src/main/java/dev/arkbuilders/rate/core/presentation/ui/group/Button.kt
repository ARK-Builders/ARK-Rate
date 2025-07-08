package dev.arkbuilders.rate.core.presentation.ui.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun ArkOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = ArkColor.TextSecondary,
                disabledContentColor = Color.White,
                disabledContainerColor = ArkColor.TextSecondary,
            ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, ArkColor.BorderSecondary),
        onClick = onClick,
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
        )
    }
}
