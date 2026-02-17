package com.example.kisanmate.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.splash.SplashAction
import presentation.splash.SplashState

// Extending the standard ViewModel for better lifecycle management
class SplashViewModel : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    fun onAction(action: SplashAction) {
        when (action) {
            SplashAction.LoadApp -> startLoading()
        }
    }

    private fun startLoading() {
        viewModelScope.launch {
            // Step 1: Initialize Local Database (SQLDelight)
            _state.update { it.copy(statusText = "INITIALIZING DATABASE", progress = 10) }
            delay(500)

            // Step 2: Simulate checking Sync Manager status
            for (i in 20..90 step 20) {
                delay(300)
                _state.update { it.copy(
                    progress = i,
                    statusText = "CHECKING SYNC STATUS $i%"
                ) }
            }

            // Step 3: Finalize Ready State
            _state.update { it.copy(statusText = "READY", progress = 100) }
            delay(400)

            _state.update { it.copy(isFinished = true) }
        }
    }
}