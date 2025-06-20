package com.example.storyapp.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.pref.StoryAppPreferences
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData

class SettingsViewModel(private val pref: StoryAppPreferences) : ViewModel() {

    fun getLangSettings(): LiveData<String> {
        return pref.getLanguageSetting().asLiveData()
    }

    fun saveLangSettings(language: String) {
        viewModelScope.launch {
            pref.saveLanguageSetting(language)
        }
    }
}