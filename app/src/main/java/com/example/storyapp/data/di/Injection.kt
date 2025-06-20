package com.example.storyapp.data.di

import android.content.Context
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.pref.StoryAppPreferences
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.data.remote.retrofit.auth.AuthApiConfig
import com.example.storyapp.data.remote.retrofit.story.StoryApiConfig
import com.example.storyapp.data.repository.AuthRepository
import com.example.storyapp.data.repository.MapsRepository
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val storyAppPreferences = StoryAppPreferences.getInstance(context.dataStore)
        val token = runBlocking{
            storyAppPreferences.getToken().first()
        }
        val apiService = StoryApiConfig.getApiService(token)
        val database = StoryDatabase.getInstance(context)
        return StoryRepository(database, apiService)
    }

    fun provideMapsRepository(context: Context): MapsRepository {
        val storyAppPreferences = StoryAppPreferences.getInstance(context.dataStore)
        val token = runBlocking{
            storyAppPreferences.getToken().first()
        }
        val apiService = StoryApiConfig.getApiService(token)
        return MapsRepository(apiService)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        val storyAppPreferences = StoryAppPreferences.getInstance(context.dataStore)
        val apiService = AuthApiConfig.getApiService()
        return AuthRepository(apiService, storyAppPreferences)
    }
}