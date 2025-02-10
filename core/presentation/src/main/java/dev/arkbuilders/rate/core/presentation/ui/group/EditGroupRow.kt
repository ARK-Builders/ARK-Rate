package dev.arkbuilders.rate.core.presentation.ui.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun EditGroupRow(onEdit: () -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.groups),
            fontWeight = FontWeight.SemiBold,
            color = ArkColor.TextSecondary,
            fontSize = 18.sp,
        )
        OutlinedButton(
            onClick = onEdit,
            border =
                BorderStroke(
                    width = 1.dp,
                    color = ArkColor.BorderSecondary,
                ),
            colors = ButtonDefaults.outlinedButtonColors(),
            shape = RoundedCornerShape(8.dp),
        ) {
            Icon(
                painter = painterResource(CoreRDrawable.ic_edit),
                contentDescription = null,
                tint = ArkColor.TextSecondary,
            )
            Text(
                text = stringResource(CoreRString.edit),
                fontWeight = FontWeight.Medium,
                color = ArkColor.TextSecondary,
            )
        }
    }
}
