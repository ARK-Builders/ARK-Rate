package dev.arkbuilders.rate.presentation.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.data.preferences.PreferenceKey
import dev.arkbuilders.rate.data.preferences.Preferences
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class SettingsViewModel(
    private val prefs: Preferences
): ViewModel() {

    var quickScreenTagCloud = mutableStateOf(false)

    var initialized by mutableStateOf(false)

    init {
        viewModelScope.launch {
            quickScreenTagCloud.value = prefs.get(PreferenceKey.QuickScreenTagCloud)
        }

        initialized = true
    }

    fun onToggle(
        key: PreferenceKey<Boolean>,
        state: MutableState<Boolean>
    ) = viewModelScope.launch {
        val newValue = state.value.not()
        state.value = newValue
        prefs.set(key, newValue)
    }

}

@Singleton
class SettingsViewModelFactory @Inject constructor(
    private val prefs: Preferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(prefs) as T
    }
}