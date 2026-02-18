package com.example.kisanmate

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kisanmate.presentation.auth.AuthViewModel
import com.example.kisanmate.presentation.splash.SplashViewModel
import com.example.kisanmate.ui.auth.AuthScreen
import com.example.kisanmate.ui.dashboard.DashboardScreen
import com.example.kisanmate.ui.splash.SplashScreen
import com.example.kisanmate.ui.dashboard.ModernBottomNav
import com.example.kisanmate.ui.reports.ReportsScreen
import com.example.kisanmate.ui.khata.KhataScreen
import com.example.kisanmate.ui.profile.ProfileScreen


@Composable
fun App(repository: ExpenseRepository) {
    val splashViewModel = remember { SplashViewModel() }
    val authViewModel = remember { AuthViewModel() }

    var currentScreen by remember { mutableStateOf("splash") }
    var selectedTab by remember { mutableStateOf("home") }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                // Fixed Bottom Bar shows only after Auth
                if (currentScreen == "main") {
                    ModernBottomNav(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentScreen) {
                    "splash" -> SplashScreen(
                        viewModel = splashViewModel,
                        onFinished = { currentScreen = "auth" }
                    )
                    "auth" -> {
                        val authState by authViewModel.state.collectAsState()

                        LaunchedEffect(authState.isAuthenticated) {
                            if (authState.isAuthenticated) currentScreen = "main"
                        }

                        AuthScreen(state = authState, onAction = { authViewModel.onAction(it) })
                    }
                    "main" -> {
                        when (selectedTab) {
                            "home" -> DashboardScreen(repository = repository)
                            "reports" -> ReportsScreen(repository = repository)
                            "khata" -> KhataScreen(repository = repository)
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
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}