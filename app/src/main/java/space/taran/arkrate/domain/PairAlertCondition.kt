package space.taran.arkrate.domain

data class PairAlertCondition(
    val id: Long,
    val numeratorCode: String,
    val denominatorCode: String,
    val ratio: Float,
    var moreNotLess: Boolean
) {
    fun isConditionMet(currentRatio: Float) =
        if (moreNotLess)
            currentRatio >= ratio
        else
            currentRatio <= ratio

    fun isCompleted() =
        numeratorCode.isNotEmpty() &&
                denominatorCode.isNotEmpty()

    companion object {
        fun defaultInstance() = PairAlertCondition(
            id = 0,
            numeratorCode = "",
            denominatorCode = "",
            ratio = 1f,
            moreNotLess = true
        )
    }
}