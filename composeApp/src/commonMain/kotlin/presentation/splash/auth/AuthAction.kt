package com.example.kisanmate.presentation.auth

sealed interface AuthAction {
    data class OnPhoneChange(val phone: String) : AuthAction
    data class OnNameChange(val name: String) : AuthAction // Added for Signup
    data class OnOtpChange(val code: String) : AuthAction
    data object SendOtp : AuthAction
    data object VerifyOtp : AuthAction
    data object ToggleAuthMode : AuthAction // Switches between Login and Signup
}