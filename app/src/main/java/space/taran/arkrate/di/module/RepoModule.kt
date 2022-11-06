package space.taran.arkrate.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import space.taran.arkrate.data.assets.AssetsRepo
import javax.inject.Singleton

@Module
class RepoModule {

    @Singleton
    @Provides
    fun userCurrencyRepo(context: Context): AssetsRepo {
        val dbPath =
            context.getExternalFilesDir("database").toString() + "/Currencies.json"
        return AssetsRepo(dbPath)
    }
}