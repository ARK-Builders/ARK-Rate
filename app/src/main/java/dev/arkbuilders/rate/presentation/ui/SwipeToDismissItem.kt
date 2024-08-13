@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.theme.ArkColor

// bug: callbacks from swipe called multiply times
@Composable
fun AppSwipeToDismiss(content: @Composable () -> Unit, onDelete: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(state = dismissState) },
        content = { content() },
        enableDismissFromStartToEnd = false
    )
}

@Composable
private fun DismissBackground(state: SwipeToDismissBoxState) {
    val color = when (state.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> ArkColor.UtilityError200
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier)
        Icon(
            modifier = Modifier.padding(end = 17.dp),
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = "",
            tint = ArkColor.FGErrorPrimary
        )
    }
}