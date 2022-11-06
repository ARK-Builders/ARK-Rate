package space.taran.arkrate.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import space.taran.arkrate.data.GeneralCurrencyRepo
import space.taran.arkrate.data.assets.AssetsRepo
import space.taran.arkrate.di.module.ApiModule
import space.taran.arkrate.di.module.RepoModule
import space.taran.arkrate.presentation.summary.SummaryViewModelFactory
import space.taran.arkrate.presentation.addcurrency.AddCurrencyViewModelFactory
import space.taran.arkrate.presentation.assets.AssetsViewModelFactory
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class,
        RepoModule::class
    ]
)
interface AppComponent {
    fun summaryViewModelFactory(): SummaryViewModelFactory
    fun assetsVMFactory(): AssetsViewModelFactory
    fun addCurrencyVMFactory(): AddCurrencyViewModelFactory
    fun generalCurrencyRepo(): GeneralCurrencyRepo
    fun assetsRepo(): AssetsRepo

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @BindsInstance context: Context
        ): AppComponent
    }
}