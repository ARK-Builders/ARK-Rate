package dev.arkbuilders.rate.feature.onboarding.pages

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.feature.onboarding.R

class PageItem(
    @DrawableRes
    val image: Int,
    @StringRes
    val title: Int,
    @StringRes
    val desc: Int?,
    @StringRes
    val button: Int,
) {
    companion object {
        fun items() =
            listOf(
                PageItem(
                    image = R.drawable.onboarding_1,
                    title = CoreRString.onboarding_1_title,
                    desc = null,
                    button = CoreRString.get_started,
                ),
                PageItem(
                    image = R.drawable.onboarding_2,
                    title = CoreRString.onboarding_2_title,
                    desc = CoreRString.onboarding_2_desc,
                    button = CoreRString.next,
                ),
                PageItem(
                    image = R.drawable.onboarding_3,
                    title = CoreRString.onboarding_3_title,
                    desc = CoreRString.onboarding_3_desc,
                    button = CoreRString.next,
                ),
                PageItem(
                    image = R.drawable.onboarding_4,
                    title = CoreRString.onboarding_4_title,
                    desc = CoreRString.onboarding_4_desc,
                    button = CoreRString.finish,
                ),
            )
    }
}
