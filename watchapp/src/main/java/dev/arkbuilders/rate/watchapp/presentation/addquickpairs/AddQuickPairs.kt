package dev.arkbuilders.rate.watchapp.presentation.addquickpairs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.watchapp.presentation.addquickpairs.composables.OptionItem

@Composable
fun AddQuickPairs(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = "Options",
                textAlign = TextAlign.Center
            )
            OptionItem(
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                icon = painterResource(id = CoreRDrawable.ic_download),
                text = stringResource(id = CoreRString.edit),
                onClick = {},
            )
        }

    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun AddQuickPairsPreview() {
    AddQuickPairs()
}
