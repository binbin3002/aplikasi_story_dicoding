package com.example.storyapp.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "StoryAppPreferences")

class StoryAppPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val languageKey = stringPreferencesKey("language_setting")
    private val tokenKey = stringPreferencesKey("token")
    private val nameKey = stringPreferencesKey("name")

    fun getLanguageSetting(): Flow<String> {
        return dataStore.data.map { preferences ->
            val lang = preferences[languageKey] ?: "en"
            lang
        }
    }

    suspend fun saveLanguageSetting(language: String) {
        dataStore.edit { preferences ->
            preferences[languageKey] = language
        }
    }

    fun getLangSettingsSync(): String {
        return runBlocking {
            dataStore.data.first()[languageKey] ?: Locale.getDefault().language
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun saveName(name: String) {
        dataStore.edit { preferences ->
            preferences[nameKey] = name
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            val token = preferences[tokenKey] ?: ""
            token
        }
    }

    fun getName(): Flow<String> {
        return dataStore.data.map { preferences ->
            val name = preferences[nameKey] ?: ""
            name
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryAppPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): StoryAppPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryAppPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}