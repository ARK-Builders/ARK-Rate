package dev.arkbuilders.rate.feature.quick.presentation.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv

@Composable
fun SwapBtn(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        AppHorDiv(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 12.dp),
        )
        OutlinedButton(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, ArkColor.BorderSecondary),
            contentPadding = PaddingValues(0.dp),
            onClick = onClick,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
        ) {
            Icon(painter = painterResource(R.drawable.ic_refresh), contentDescription = null)
        }
        AppHorDiv(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 16.dp),
        )
    }
}
