package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.foundation.pager.PagerState

/** See:
 * rememberPagerState()
 * androidx.compose.foundation.pager.DefaultPagerState
 */

class RatePagerState(
    currentPage: Int = 0,
    currentPageOffsetFraction: Float = 0f,
    private val updatedPageCount: () -> Int,
) : PagerState(currentPage, currentPageOffsetFraction) {
    override val pageCount: Int get() = updatedPageCount()
}
