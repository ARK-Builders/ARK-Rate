package dev.arkbuilders.rate.watchapp.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.SearchTextField

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    ScalingLazyColumn(
        modifier = modifier.fillMaxSize().background(ArkColor.BGSecondaryAlt),
        contentPadding = PaddingValues(4.dp)
    ) {
        item {
            SearchTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp))
        }
    }
}
