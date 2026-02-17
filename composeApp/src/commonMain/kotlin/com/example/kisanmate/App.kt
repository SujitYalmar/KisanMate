package com.example.kisanmate

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.kisanmate.presentation.splash.SplashViewModel
import com.example.kisanmate.ui.splash.SplashScreen
import com.example.kisanmate.ui.auth.AuthScreen
import com.example.kisanmate.presentation.auth.AuthViewModel

@Composable
fun App(repository: ExpenseRepository) {
    // 1. Initialize ViewModels
    val splashViewModel = remember { SplashViewModel() }
    val authViewModel = remember { AuthViewModel() }

    // 2. Navigation State
    var currentScreen by remember { mutableStateOf("splash") }

    MaterialTheme {
        when (currentScreen) {
            "splash" -> {
                SplashScreen(
                    viewModel = splashViewModel,
                    onFinished = {
                        // Move to Auth screen after splash
                        currentScreen = "auth"
                    }
                )
            }
            "auth" -> {
                val authState by authViewModel.state.collectAsState()
                AuthScreen(
                    state = authState,
                    onAction = { action ->
                        authViewModel.onAction(action)
                        // If authenticated, move to dashboard
                        if (authState.isAuthenticated) {
                            currentScreen = "dashboard"
                        }
                    }
                )
            }
            "dashboard" -> {
                // Now passing the real repository for offline data
                DashboardScreen(repository = repository)
            }
        }
    }
}

@Composable
fun DashboardScreen(repository: ExpenseRepository) {
    // UI code for your expense dashboard goes here
}