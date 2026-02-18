package com.example.kisanmate.presentation.auth

sealed interface AuthAction {
    data class OnPhoneChange(val phone: String) : AuthAction
    data class OnOtpChange(val code: String) : AuthAction
    data object SendOtp : AuthAction // Triggers the SMS
    data object VerifyOtp : AuthAction // Validates the code
}