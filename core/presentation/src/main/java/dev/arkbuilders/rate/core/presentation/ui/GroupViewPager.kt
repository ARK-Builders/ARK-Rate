@file:OptIn(ExperimentalFoundationApi::class)

package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import kotlinx.coroutines.launch

@Composable
fun GroupViewPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    groups: List<Group>,
    onEditGroups: () -> Unit,
    pageContent: @Composable (index: Int) -> Unit,
) {
    Column(modifier = modifier) {
        Tabs(pagerState, groups, onEditGroups)
        HorizontalPager(state = pagerState) {
            pageContent(it)
        }
    }
}

@Composable
private fun Tabs(
    pagerState: PagerState,
    groups: List<Group>,
    onEditGroups: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Box(Modifier.fillMaxWidth().height(52.dp)) {
        AppHorDiv(modifier = Modifier.align(Alignment.BottomCenter))
        ScrollableTabRow(
            modifier = Modifier.align(Alignment.BottomCenter).padding(end = 44.dp),
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
                        text = group.name,
                        color = if (selected) ArkColor.Teal700 else ArkColor.TextQuarterary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Box(modifier = Modifier.height(8.dp))
                }
            }
        }
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .height(52.dp)
                .width(130.dp)
                .background(
                    brush =
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = .8f),
                                Color.White,
                            ),
                        ),
                ),
        )
        IconButton(
            modifier =
                Modifier
                    .size(48.dp)
                    .align(Alignment.CenterEnd),
            onClick = onEditGroups,
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = null,
                tint = ArkColor.NeutralGray700,
            )
        }
    }
}
