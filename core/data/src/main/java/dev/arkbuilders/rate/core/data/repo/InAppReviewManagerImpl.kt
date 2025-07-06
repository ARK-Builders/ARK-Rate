package dev.arkbuilders.rate.core.data.repo

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory
import dev.arkbuilders.rate.core.domain.repo.InAppReviewManager
import kotlinx.coroutines.tasks.await

class InAppReviewManagerImpl : InAppReviewManager {
    override suspend fun launchReview(activity: Activity): Boolean {
        return try {
            val reviewManager = ReviewManagerFactory.create(activity)
            val request = reviewManager.requestReviewFlow()
            val reviewInfo = request.await()

            reviewManager.launchReviewFlow(activity, reviewInfo).await()

            true
        } catch (_: Throwable) {
            false
        }
    }
}
