package com.example.storyapp.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.response.story.ListStoryItem
import com.example.storyapp.data.remote.retrofit.story.StoryApiConfig
import kotlinx.coroutines.withContext
import java.lang.Exception

class WidgetViewModel : ViewModel() {
    suspend fun fetchStories(token: String, page: Int, size: Int, location: Int): List<ListStoryItem>? {
        return withContext(viewModelScope.coroutineContext) {
            try {
                val response = StoryApiConfig.getApiService(token).getStories(page, size, location)
                if (response.isSuccessful) {
                    response.body()?.listStory
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}