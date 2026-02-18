package com.example.kisanmate.presentation.auth

data class AuthState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val isOtpSent: Boolean = false, // Controls the "flip" to the OTP screen
    val isAuthenticated: Boolean = false,
    val error: String? = null
)