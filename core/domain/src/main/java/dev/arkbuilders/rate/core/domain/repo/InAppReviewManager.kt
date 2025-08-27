package dev.arkbuilders.rate.core.domain.repo

import android.app.Activity

interface InAppReviewManager {
    suspend fun launchReview(activity: Activity): Boolean
}
