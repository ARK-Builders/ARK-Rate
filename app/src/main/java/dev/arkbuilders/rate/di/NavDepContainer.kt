package dev.arkbuilders.rate.di

import androidx.lifecycle.SavedStateHandle
import dev.arkbuilders.rate.presentation.MainActivity
import dev.arkbuilders.rate.presentation.shared.SharedViewModel

class NavDepContainer(val activity: MainActivity) {
    @Suppress("UNCHECKED_CAST")
    fun <T> createViewModel(modelClass: Class<T>, handle: SavedStateHandle): T {
        return when (modelClass) {
            SharedViewModel::class.java -> DIManager.component.sharedVMFactory().create(modelClass)
            else -> throw RuntimeException("Unknown view model $modelClass")
        } as T
    }
}