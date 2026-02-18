package com.example.kisanmate.presentation.auth

data class AuthState(
    val phoneNumber: String = "",
    val name: String = "", // Added for Signup
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val isOtpSent: Boolean = false,
    val isSignupMode: Boolean = false, // Toggle state
    val isAuthenticated: Boolean = false,
    val error: String? = null
)