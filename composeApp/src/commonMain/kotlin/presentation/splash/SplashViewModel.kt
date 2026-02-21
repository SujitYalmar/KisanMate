package com.example.kisanmate.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.splash.SplashAction
import presentation.splash.SplashState

class SplashViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    fun onAction(action: SplashAction) {
        when (action) {
            SplashAction.LoadApp -> startLoading()
        }
    }

    private fun startLoading() {
        viewModelScope.launch {

            delay(800)

            val currentUser = firebaseAuth.currentUser

            _state.update {
                it.copy(
                    isFinished = true,
                    isUserLoggedIn = currentUser != null
                )
            }
        }
    }
}