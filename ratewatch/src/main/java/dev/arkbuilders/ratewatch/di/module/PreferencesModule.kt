package dev.arkbuilders.ratewatch.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.arkbuilders.ratewatch.data.preferences.PrefsImpl
import dev.arkbuilders.ratewatch.domain.repo.Prefs
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): Prefs {
        return PrefsImpl(context)
    }
}
