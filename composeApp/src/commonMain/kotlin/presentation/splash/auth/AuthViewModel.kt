package com.example.kisanmate.presentation.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnPhoneChange -> {
                _state.value = _state.value.copy(phoneNumber = action.phone)
            }
            AuthAction.OnLoginClick -> {
                // Temporary: direct login
                _state.value = _state.value.copy(isAuthenticated = true)
            }
        }
    }
}
