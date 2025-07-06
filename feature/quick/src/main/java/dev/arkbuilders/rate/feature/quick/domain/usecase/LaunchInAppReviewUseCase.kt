package dev.arkbuilders.rate.feature.quick.domain.usecase

import android.app.Activity
import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider
import dev.arkbuilders.rate.core.domain.repo.InAppReviewManager
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import timber.log.Timber
import javax.inject.Inject

class LaunchInAppReviewUseCase @Inject constructor(
    private val inAppReviewManager: InAppReviewManager,
    private val prefs: Prefs,
    private val quickRepo: QuickRepo,
    private val buildConfigFieldsProvider: BuildConfigFieldsProvider,
) {
    suspend operator fun invoke(activity: Activity) {
        val buildConfig = buildConfigFieldsProvider.provide()
        if (buildConfig.isGooglePlayBuild.not()) {
            Timber.d("In-app review skipped: not Google Play build")
            return
        }

        Timber.d("Quick feature: in-app review flow started")

        val moreThan2Pairs = quickRepo.getAll().size > 2
        if (moreThan2Pairs.not()) return

//        val isFirstLaunch = false
//        if (isFirstLaunch) return

        val attemptCount = prefs.get(PreferenceKey.InAppReviewAttemptCount)
        val moreThan10Attempts = attemptCount > 10
        if (moreThan10Attempts) return

        val shown = inAppReviewManager.launchReview(activity)
        if (shown) {
            prefs.set(PreferenceKey.InAppReviewAttemptCount, attemptCount + 1)
        }
    }
}
