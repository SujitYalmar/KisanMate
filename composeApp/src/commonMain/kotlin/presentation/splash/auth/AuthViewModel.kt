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
            is AuthAction.OnOtpChange -> _state.update { it.copy(otpCode = action.code) }
            AuthAction.SendOtp -> simulateSendOtp()
            AuthAction.VerifyOtp -> simulateVerifyOtp()
        }
    }

    private fun simulateSendOtp() {
        viewModelScope.launch {
            if (_state.value.phoneNumber.length != 10) {
                _state.update { it.copy(error = "Enter 10-digit number") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }
            delay(1500) // Simulate network delay

            _state.update { it.copy(isLoading = false, isOtpSent = true) }
        }
    }

    private fun simulateVerifyOtp() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            delay(1000) // Simulate verification

            if (_state.value.otpCode == "123456") {
                // Success: Navigates to Dashboard via LaunchedEffect in App.kt
                _state.update { it.copy(isLoading = false, isAuthenticated = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = "Wrong OTP! Try 123456") }
            }
        }
    }
}