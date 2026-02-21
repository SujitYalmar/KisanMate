package com.example.kisanmate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.kisanmate.presentation.auth.AuthViewModel
import com.example.kisanmate.presentation.splash.SplashViewModel
import com.example.kisanmate.ui.auth.AuthScreen
import com.example.kisanmate.ui.dashboard.DashboardScreen
import com.example.kisanmate.ui.dashboard.ModernBottomNav
import com.example.kisanmate.ui.khata.KhataScreen
import com.example.kisanmate.ui.profile.ProfileScreen
import com.example.kisanmate.ui.reports.ReportsScreen
import com.example.kisanmate.ui.splash.SplashScreen
import com.example.kisanmate.ui.khata.AllTransactionsScreen

@Composable
fun App(
    repository: ExpenseRepository,
    authViewModel: AuthViewModel
) {

    val splashViewModel = remember { SplashViewModel() }

    var currentScreen by remember { mutableStateOf("splash") }
    var selectedTab by remember { mutableStateOf("home") }

    MaterialTheme {

        Scaffold(
            bottomBar = {
                if (currentScreen == "main") {
                    ModernBottomNav(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            selectedTab = tab
                        },
                        onFabClick = {
                            selectedTab = "home"
                        }
                    )
                }
            }
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                when (currentScreen) {

                    // ✅ FIXED SPLASH LOGIC
                    "splash" -> {

                        val splashState by splashViewModel.state.collectAsState()

                        LaunchedEffect(splashState.isFinished) {
                            if (splashState.isFinished) {
                                currentScreen =
                                    if (splashState.isUserLoggedIn) "main"
                                    else "auth"
                            }
                        }

                        SplashScreen(
                            viewModel = splashViewModel
                        )
                    }

                    "auth" -> {

                        val authState by authViewModel.state.collectAsState()

                        LaunchedEffect(authState.isAuthenticated) {
                            if (authState.isAuthenticated) {
                                currentScreen = "main"
                            }
                        }

                        AuthScreen(
                            state = authState,
                            onAction = { authViewModel.onAction(it) }
                        )
                    }

                    "main" -> {

                        when (selectedTab) {

                            "home" -> DashboardScreen(
                                repository = repository,
                                onViewAllClick = {
                                    selectedTab = "khata"
                                }
                            )

                            "reports" -> ReportsScreen(repository)

                            "khata" -> AllTransactionsScreen(
                                repository = repository,
                                onBack = {
                                    selectedTab = "home"
                                }
                            )

                            "profile" -> ProfileScreen(
                                onLogout = {
                                    selectedTab = "home"
                                    currentScreen = "auth"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}