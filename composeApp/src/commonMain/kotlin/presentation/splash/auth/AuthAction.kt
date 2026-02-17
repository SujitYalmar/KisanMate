package com.example.kisanmate.presentation.auth

sealed class AuthAction {
    data class OnPhoneChange(val phone: String) : AuthAction()
    data object OnLoginClick : AuthAction()
}
