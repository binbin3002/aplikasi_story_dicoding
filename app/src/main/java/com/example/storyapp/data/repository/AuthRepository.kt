package com.example.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.storyapp.data.pref.StoryAppPreferences
import com.example.storyapp.data.remote.response.auth.LoginResponse
import com.example.storyapp.data.remote.retrofit.auth.AuthApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class AuthRepository(
    private val apiService: AuthApiService,
    private val storyAppPreferences: StoryAppPreferences
) {
    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postLogin(email, password)
            if (response.isSuccessful) {
                Log.d("AuthRepository", "Login response: ${response.body()}")
                storyAppPreferences.saveToken(
                    token = response.body()?.loginResult?.token ?: "",
                )
                storyAppPreferences.saveName(
                    name = response.body()?.loginResult?.name ?: "",
                )
                val token = storyAppPreferences.getToken().first()
                val name = storyAppPreferences.getName().first()
                if (token.isNotEmpty() && name.isNotEmpty()) {
                    emit(Result.Success(response.body() ?: LoginResponse()))
                } else {
                    emit(Result.Error("Failed to save session"))
                }
            } else {
                emit(Result.Error("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun getToken(): Flow<Result<String>> = flow {
        emit(Result.Loading)
        val token = storyAppPreferences.getToken().first()
        if (token.isNotEmpty()) {
            emit(Result.Success(token))
        } else {
            emit(Result.Error("You're not logged in"))
        }
    }.catch { e ->
        emit(Result.Error(e.message ?: "Unknown error"))
    }
    fun getName(): Flow<Result<String>> = flow {
        emit(Result.Loading)
        val name = storyAppPreferences.getName().first()
        if (name.isNotEmpty()) {
            emit(Result.Success(name))
        } else {
            emit(Result.Error("No name found"))
        }
    }.catch { e ->
        emit(Result.Error(e.message ?: "Unknown error"))
    }

    fun logout(): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        storyAppPreferences.saveToken(token = "")
        storyAppPreferences.saveName(name = "")
        val token = storyAppPreferences.getToken().first()
        val name = storyAppPreferences.getName().first()
        if (token.isEmpty() && name.isEmpty()) {
            emit(Result.Success(true))
        } else {
            emit(Result.Error("Failed to logout"))
        }
    }.catch { e ->
        emit(Result.Error(e.message ?: "Unknown error"))
    }

}