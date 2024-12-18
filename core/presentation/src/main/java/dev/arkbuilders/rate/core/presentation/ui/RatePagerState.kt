package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.foundation.pager.PagerState

/** androidx.compose.foundation.pager.DefaultPagerState **/

class RatePagerState(
    currentPage: Int = 0,
    currentPageOffsetFraction: Float = 0f,
) : PagerState(currentPage, currentPageOffsetFraction) {
    private var _pageCount = 0
    override val pageCount: Int get() = _pageCount

    fun setPageCount(count: Int) {
        _pageCount = count
    }
}
