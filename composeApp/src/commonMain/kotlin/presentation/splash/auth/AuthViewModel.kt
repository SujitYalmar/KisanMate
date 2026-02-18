package com.example.kisanmate.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnPhoneChange -> {
                val cleaned = action.phone.filter { it.isDigit() }
                _state.update { it.copy(phoneNumber = cleaned) }
            }
            // 1. Handle Name Input
            is AuthAction.OnNameChange -> {
                _state.update { it.copy(name = action.name) }
            }
            is AuthAction.OnOtpChange -> _state.update { it.copy(otpCode = action.code) }

            // 2. Toggle between Login and Signup
            AuthAction.ToggleAuthMode -> {
                _state.update { it.copy(
                    isSignupMode = !it.isSignupMode,
                    error = null // Clear errors when switching
                )}
            }
            AuthAction.SendOtp -> simulateSendOtp()
            AuthAction.VerifyOtp -> simulateVerifyOtp()
        }
    }

    private fun simulateSendOtp() {
        viewModelScope.launch {
            val s = _state.value

            // Validation for Signup vs Login
            if (s.isSignupMode && s.name.isBlank()) {
                _state.update { it.copy(error = "Please enter your full name") }
                return@launch
            }
            if (s.phoneNumber.length != 10) {
                _state.update { it.copy(error = "Enter a valid 10-digit number") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }
            delay(1500) // Simulate network request

            _state.update { it.copy(isLoading = false, isOtpSent = true) }
        }
    }

    private fun simulateVerifyOtp() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            delay(1000)

            // Success simulation: "123456" is the master code for testing
            if (_state.value.otpCode == "123456") {
                _state.update { it.copy(isLoading = false, isAuthenticated = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = "Invalid OTP. Use 123456") }
            }
        }
    }
}