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
fun AuthScreen(state: AuthState, onAction: (AuthAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF7E7))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text("KisanMate", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF795548))

        Text(
            text = when {
                state.isOtpSent -> "Verify OTP"
                state.isSignupMode -> "Create Account"
                else -> "Welcome Back!"
            },
            fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4E342E)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Input Card
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFD7CCC8)),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // NAME FIELD: Only shows during Signup Mode
                if (state.isSignupMode && !state.isOtpSent) {
                    AuthInputField(
                        label = "Full Name",
                        value = state.name,
                        onValueChange = { onAction(AuthAction.OnNameChange(it)) },
                        placeholder = "e.g. Sujit Yalmar",
                        keyboardType = KeyboardType.Text // Standard text for names
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // PHONE/OTP FIELD
                AuthInputField(
                    label = if (state.isOtpSent) "6-Digit Code" else "Mobile Number",
                    value = if (state.isOtpSent) state.otpCode else state.phoneNumber,
                    onValueChange = {
                        if (state.isOtpSent) onAction(AuthAction.OnOtpChange(it))
                        else onAction(AuthAction.OnPhoneChange(it))
                    },
                    placeholder = if (state.isOtpSent) "000000" else "9876543210",
                    keyboardType = KeyboardType.Number // Numeric for phone/OTP
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ERROR DISPLAY: Shows Firebase or validation errors
        state.error?.let {
            Text(text = it, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        Button(
            onClick = {
                if (state.isOtpSent) onAction(AuthAction.VerifyOtp)
                else onAction(AuthAction.SendOtp)
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5B31)),
            shape = RoundedCornerShape(30.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text(if (state.isOtpSent) "Verify & Start" else "Get OTP", fontSize = 18.sp)
        }

        // Toggle Auth Mode: Login <-> Signup
        if (!state.isOtpSent) {
            TextButton(onClick = { onAction(AuthAction.ToggleAuthMode) }) {
                Text(
                    text = if (state.isSignupMode) "Already have an account? Login"
                    else "New Farmer? Create an Account",
                    color = Color(0xFF795548)
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
    keyboardType: KeyboardType = KeyboardType.Number // Default to number
) {
    Column {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4E342E)),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) Text(placeholder, color = Color.LightGray, fontSize = 24.sp)
                innerTextField()
            }
        )
    }
}