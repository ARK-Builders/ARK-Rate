package dev.arkbuilders.ratewatch.domain.model

data class QuickScreenPage(
    val group: String?,
    val pinned: List<PinnedQuickPair>,
    val notPinned: List<QuickPair>,
)
