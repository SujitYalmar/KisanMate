package com.example.kisanmate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.kisanmate.presentation.auth.AuthViewModel
import com.example.kisanmate.presentation.splash.SplashViewModel
import com.example.kisanmate.ui.auth.AuthScreen
import com.example.kisanmate.ui.dashboard.DashboardScreen
import com.example.kisanmate.ui.splash.SplashScreen
import com.example.kisanmate.ui.reports.ReportsScreen
import com.example.kisanmate.ui.khata.KhataScreen
import com.example.kisanmate.ui.profile.ProfileScreen

@Composable
fun App(
    repository: ExpenseRepository,
    authViewModel: AuthViewModel
) {
    val splashViewModel = remember { SplashViewModel() }

    var currentScreen by remember { mutableStateOf("splash") }
    var selectedTab by remember { mutableStateOf("home") }

    MaterialTheme {
        Scaffold { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {

                    "splash" -> SplashScreen(
                        viewModel = splashViewModel,
                        onFinished = { currentScreen = "auth" }
                    )

                    "auth" -> {
                        val authState by authViewModel.state.collectAsState()

                        LaunchedEffect(authState.isAuthenticated) {
                            if (authState.isAuthenticated) {
                                currentScreen = "main"
                            }
                        }

                        AuthScreen(
                            state = authState,
                            onAction = { action ->
                                authViewModel.onAction(action)
                            }
                        )
                    }

                    "main" -> {
                        when (selectedTab) {
                            "home" -> DashboardScreen(repository)
                            "reports" -> ReportsScreen(repository)
                            "khata" -> KhataScreen(repository)
                            "profile" -> ProfileScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CenterText(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}