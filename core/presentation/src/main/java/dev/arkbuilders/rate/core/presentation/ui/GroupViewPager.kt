@file:OptIn(ExperimentalFoundationApi::class)

package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import kotlinx.coroutines.launch

@Composable
fun GroupViewPager(
    modifier: Modifier = Modifier,
    groups: List<String?>,
    pageContent: @Composable (index: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { groups.size }
    Column(modifier = modifier) {
        Box {
            AppHorDiv(modifier = Modifier.align(Alignment.BottomCenter))
            ScrollableTabRow(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                containerColor = Color.Transparent,
                selectedTabIndex = pagerState.currentPage,
                divider = { },
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier =
                            Modifier
                                .tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = ArkColor.Teal500,
                    )
                },
            ) {
                groups.forEachIndexed { index, group ->
                    val selected = index == pagerState.currentPage
                    Tab(
                        selected = selected,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        selectedContentColor = Color.Transparent,
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 6.dp),
                            text = group ?: stringResource(R.string.group_default_name),
                            color = if (selected) ArkColor.Teal700 else ArkColor.TextQuarterary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Box(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
        HorizontalPager(state = pagerState) {
            pageContent(it)
        }
    }
}
