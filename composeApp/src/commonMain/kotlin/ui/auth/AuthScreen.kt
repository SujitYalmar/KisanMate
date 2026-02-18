package com.example.kisanmate.ui.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kisanmate.presentation.auth.*

@Composable
fun AuthScreen(
    state: AuthState,
    onAction: (AuthAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF7E7)) // Cream background
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        // 1. Welcome Header (Text only)
        Text(
            text = "KisanMate",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF795548),
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (state.isOtpSent) "Verify OTP" else "Welcome, Farmer!",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4E342E)
        )

        Spacer(modifier = Modifier.height(60.dp))

        // 2. Custom Input Card
        Surface(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFD7CCC8)),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (state.isOtpSent) "6-Digit Code" else "Mobile Number",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                BasicTextField(
                    value = if (state.isOtpSent) state.otpCode else state.phoneNumber,
                    onValueChange = {
                        if (state.isOtpSent) onAction(AuthAction.OnOtpChange(it))
                        else onAction(AuthAction.OnPhoneChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4E342E)
                    ),
                    decorationBox = { innerTextField ->
                        if ((!state.isOtpSent && state.phoneNumber.isEmpty()) ||
                            (state.isOtpSent && state.otpCode.isEmpty())) {
                            Text(
                                text = if (state.isOtpSent) "000000" else "e.g. 9876543210",
                                color = Color.LightGray,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Main Action Button
        Button(
            onClick = {
                if (state.isOtpSent) onAction(AuthAction.VerifyOtp)
                else onAction(AuthAction.SendOtp)
            },
            modifier = Modifier.fillMaxWidth().height(65.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5B31)), // Dark Green
            shape = RoundedCornerShape(32.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = if (state.isOtpSent) "Verify & Login" else "Send OTP",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 4. Status/Error Text
        state.error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }
    }
}