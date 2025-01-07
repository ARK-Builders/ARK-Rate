package dev.arkbuilders.rate.watchapp.presentation.addquickpairs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Text

@Composable
fun AddQuickPairsScreen(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    ScalingLazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = "Add",
                textAlign = TextAlign.Center
            )
        }

        item {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = "From",
            )
        }

        item {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                scrollState = scrollState
            ) {
                repeat(8) {
                    DropdownMenuItem(
                        text = { Text("Item ${it + 1}") },
                        onClick = { /* TODO */ },
                        leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) }
                    )
                }
            }
        }
        item {
            BasicTextField(
                modifier = modifier.fillMaxWidth(),
                value = "",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { text ->
                },
            )
        }

    }
}

@Composable
fun AddQuickPairsScreenPreview() {
    AddQuickPairsScreen()
}
