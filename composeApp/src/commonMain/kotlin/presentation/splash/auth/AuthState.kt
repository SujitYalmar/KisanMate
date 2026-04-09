package com.example.kisanmate.presentation.auth

data class AuthState(

    val name: String = "",

    val phoneNumber: String = "",

    val otpCode: String = "",

    val isOtpSent: Boolean = false,

    // ✅ Signup first
    val isSignupMode: Boolean = true,

    val isLoading: Boolean = false,

    val isAuthenticated: Boolean = false,

    val error: String? = null
)