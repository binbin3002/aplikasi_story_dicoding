package com.example.storyapp.data.pref

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.di.Injection
import com.example.storyapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.storyapp.data.repository.Result

class SessionViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _logoutState = MutableStateFlow<Boolean>(false)
    val logoutState: StateFlow<Boolean> = _logoutState

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _logoutState.value = true
                    }

                    is Result.Error -> {
                        _logoutState.value = false
                    }

                    is Result.Loading -> {
                        _logoutState.value = false
                    }
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SessionViewModelFactory(
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            return SessionViewModel(Injection.provideAuthRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}