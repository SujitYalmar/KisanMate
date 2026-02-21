package com.example.kisanmate.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kisanmate.presentation.splash.SplashViewModel
import presentation.splash.SplashAction

val GoogleGreen = Color(0xFF1E8E3E)
val LightBgGradient = listOf(Color(0xFFFFFFFF), Color(0xFFF1F8F1))

@Composable
fun SplashScreen(
    viewModel: SplashViewModel
) {
    // Logic remains untouched
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onAction(SplashAction.LoadApp)
    }

    // --- ANIMATION LOGIC ---
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")

    // Scale animation (1.0 to 1.1)
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoScale"
    )

    // Alpha/Opacity animation (0.6 to 1.0)
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(LightBgGradient)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Logo
            Surface(
                shape = CircleShape,
                color = GoogleGreen.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale) // Applies the pulse
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Spa,
                        contentDescription = "Logo",
                        tint = GoogleGreen.copy(alpha = alpha), // Applies the fade
                        modifier = Modifier.size(70.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Brand Name
            Text(
                text = "KisanMate",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF202124),
                letterSpacing = (-1).sp,
                modifier = Modifier.scale(scale * 0.95f) // Subtle text pulse
            )

            Text(
                text = "Smart Farming Assistant",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GoogleGreen,
                letterSpacing = 2.sp
            )
        }

        // Minimalist Secure Tag at the very bottom
        Text(
            text = "SECURE & ENCRYPTED",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            letterSpacing = 1.sp
        )
    }
}