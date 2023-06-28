package space.taran.arkrate.di

import androidx.lifecycle.SavedStateHandle
import space.taran.arkrate.presentation.MainActivity
import space.taran.arkrate.presentation.shared.SharedViewModel

class NavDepContainer(
    val activity: MainActivity
) {
    @Suppress("UNCHECKED_CAST")
    fun <T> createViewModel(modelClass: Class<T>, handle: SavedStateHandle): T {
        return when (modelClass) {
            SharedViewModel::class.java -> DIManager.component.sharedVMFactory().create(modelClass)
            else -> throw RuntimeException("Unknown view model $modelClass")
        } as T
    }
}