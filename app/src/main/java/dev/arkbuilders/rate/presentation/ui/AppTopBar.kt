@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.theme.ArkColor

@Preview(showBackground = true)
@Composable
fun AppTopBarBack(
    title: String = "Title",
    navigator: DestinationsNavigator = EmptyDestinationsNavigator,
) {
    Column {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = ArkColor.TextPrimary,
                    fontSize = 24.sp,
                )
            },
            navigationIcon = {
                IconButton(onClick = { navigator.popBackStack() }) {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "",
                        tint = ArkColor.FGSecondary,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        )
        AppHorDiv()
    }
}

@Preview(showBackground = true)
@Composable
fun AppTopBarCenterTitle(title: String = "Title") {
    Column {
        TopAppBar(
            title = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Alerts",
                        fontWeight = FontWeight.SemiBold,
                        color = ArkColor.TextPrimary,
                        fontSize = 24.sp,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        )
        AppHorDiv()
    }
}
