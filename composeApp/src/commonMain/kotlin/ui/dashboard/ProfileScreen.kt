package com.example.kisanmate.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {

    val firebaseAuth = FirebaseAuth.getInstance()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        firebaseAuth.signOut()
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Yes, Logout", color = Color(0xFFC62828))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBF8)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Profile Image & Info
        Box(
            Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            Text("👨🏽‍🌾", modifier = Modifier.align(Alignment.Center), fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Sujit Ji",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3E2723)
        )

        Text(
            "Farmer",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileOption(Icons.Default.Business, "Business Settings")
            ProfileOption(Icons.Default.Language, "Change Language")
            ProfileOption(Icons.Default.Help, "Help & Support")

            // 🔐 Logout Option (Clickable)
            ProfileOption(
                icon = Icons.Default.Logout,
                label = "Logout",
                textColor = Color(0xFFC62828),
                onClick = { showLogoutDialog = true }
            )
        }
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    label: String,
    textColor: Color = Color(0xFF3E2723),
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            },
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = textColor, modifier = Modifier.size(24.dp))
            Text(
                label,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}