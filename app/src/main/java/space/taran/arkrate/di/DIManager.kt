package space.taran.arkrate.di

import android.app.Application

object DIManager {
    lateinit var component: AppComponent
        private set

    fun init(app: Application) {
        component = DaggerAppComponent.factory().create(app, app.applicationContext)
    }
}