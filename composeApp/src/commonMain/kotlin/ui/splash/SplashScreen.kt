package com.example.kisanmate.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kisanmate.presentation.splash.SplashViewModel
import presentation.splash.*

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onAction(SplashAction.LoadApp)
    }

    if (state.isFinished) {
        onFinished()
    }

    // Use Box to fill the screen and set overall alignment
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF7E7)),
        contentAlignment = Alignment.Center // Centers the Column vertically and horizontally
    ) {
        // Main content column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center, // Ensures internal spacing is centered
            modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp)
        ) {

            // 1. Logo and Branding Group (Centered vertically)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f) // Takes up available space to keep it centered
            ) {
                // Logo placeholder (Use painterResource(Res.drawable.kisan_logo) when ready)
                Text("🌾", fontSize = 100.sp)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "KisanMate",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4E342E)
                )
                Text(
                    text = "EMPOWERING INDIAN FARMERS",
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = Color(0xFF795548)
                )
            }

            // 2. Progress and Footer Group (Pinned to bottom)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(state.statusText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D6E63))
                    Text("${state.progress.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = state.progress / 100f,
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE0E0E0)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("🛡️ Safe & Secure for Farmers", fontSize = 12.sp, color = Color(0xFF795548))
            }
        }
    }
}