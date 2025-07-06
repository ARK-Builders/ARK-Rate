package dev.arkbuilders.rate.core.data.repo

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory
import dev.arkbuilders.rate.core.domain.BuildConfigFields
import dev.arkbuilders.rate.core.domain.repo.InAppReviewManager
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class GooglePlayInAppReviewManagerImpl(
    private val buildConfigFields: BuildConfigFields,
) : InAppReviewManager {
    override suspend fun launchReview(activity: Activity): Boolean {
        if (buildConfigFields.isGooglePlayBuild.not()) {
            Timber.d("In-app review skipped: not Google Play build")
            return false
        }

        return try {
            val reviewManager = ReviewManagerFactory.create(activity)
            val request = reviewManager.requestReviewFlow()
            val reviewInfo = request.await()

            reviewManager.launchReviewFlow(activity, reviewInfo).await()

            Timber.d("Google Play in-app review launched successfully")
            true
        } catch (e: Throwable) {
            Timber.d("Google Play in-app review failed: ${e.message}")
            false
        }
    }
}
