package com.example.storyapp.data.pref


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.ui.settings.SettingsViewModel
import  java.lang.IllegalArgumentException


@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(
    private val pref: StoryAppPreferences
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: SettingsViewModelFactory? = null
        fun getInstance( pref: StoryAppPreferences): SettingsViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SettingsViewModelFactory(pref)
            }.also { instance = it }
    }
}