package com.example.kisanmate.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kisanmate.presentation.auth.*

// Design Palette
val GoogleBlue = Color(0xFF1A73E8)
val GoogleGreen = Color(0xFF1E8E3E)
val SoftBg = Color(0xFFF8F9FA)
val DarkText = Color(0xFF202124)

@Composable
fun AuthScreen(state: AuthState, onAction: (AuthAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // App Branding
        Surface(
            shape = CircleShape,
            color = GoogleGreen.copy(alpha = 0.1f),
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Spa, null, tint = GoogleGreen, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "KisanMate",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = GoogleGreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when {
                state.isOtpSent -> "Verify Code"
                state.isSignupMode -> "Join Us"
                else -> "Welcome Back"
            },
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DarkText
        )

        Text(
            text = if (state.isOtpSent) "Enter the 6-digit code sent to your phone"
            else "Enter your details to continue",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Modern Input Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = SoftBg,
            border = BorderStroke(1.dp, Color(0xFFE8EAED))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // NAME FIELD
                AnimatedVisibility(visible = state.isSignupMode && !state.isOtpSent) {
                    Column {
                        AuthInputField(
                            label = "FULL NAME",
                            value = state.name,
                            onValueChange = { onAction(AuthAction.OnNameChange(it)) },
                            placeholder = "Sujit Yalmar",
                            keyboardType = KeyboardType.Text
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(color = Color(0xFFDADCE0), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // PHONE/OTP FIELD
                AuthInputField(
                    label = if (state.isOtpSent) "OTP CODE" else "MOBILE NUMBER",
                    value = if (state.isOtpSent) state.otpCode else state.phoneNumber,
                    onValueChange = {
                        if (state.isOtpSent) onAction(AuthAction.OnOtpChange(it))
                        else onAction(AuthAction.OnPhoneChange(it))
                    },
                    placeholder = if (state.isOtpSent) "000000" else "9876543210",
                    keyboardType = KeyboardType.Number
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ERROR DISPLAY
        state.error?.let {
            Surface(
                color = Color(0xFFFFEBEE),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
            ) {
                Text(
                    text = it,
                    color = Color(0xFFC62828),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Primary Action Button
        Button(
            onClick = {
                if (state.isOtpSent) onAction(AuthAction.VerifyOtp)
                else onAction(AuthAction.SendOtp)
            },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoogleGreen),
            shape = RoundedCornerShape(20.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (state.isOtpSent) "Verify & Get Started" else "Send OTP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Rounded.ArrowForward, null, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Mode Toggler
        if (!state.isOtpSent) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { onAction(AuthAction.ToggleAuthMode) },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = if (state.isSignupMode) "Already have an account? Sign in"
                    else "New to KisanMate? Create account",
                    color = GoogleBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AuthInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType
) {
    Column {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = DarkText,
                letterSpacing = if (keyboardType == KeyboardType.Number) 4.sp else 0.sp
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(placeholder, color = Color.LightGray, fontSize = 24.sp)
                    }
                    innerTextField()
                }
            }
        )
    }
}