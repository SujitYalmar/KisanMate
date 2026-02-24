package com.example.kisanmate.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
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
import com.google.firebase.firestore.FirebaseFirestore

// Theme Palette
private val SageGreen = Color(0xFF65AD68)
private val DeepMoss = Color(0xFF2E3D2F)
private val Terracotta = Color(0xFFBF6B5D)
private val SandBg = Color(0xFFF9F7F2)
private val SurfaceCream = Color(0xFFFFFFFF)

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val user = firebaseAuth.currentUser

    var name by remember { mutableStateOf("Loading...") }
    var phone by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        user?.uid?.let { uid ->
            firestore.collection("users").document(uid).get().addOnSuccessListener {
                name = it.getString("name") ?: "Farmer"
                phone = it.getString("phone") ?: ""
            }
        }
    }

    // Modern Edit Dialog
    if (showEditDialog) {
        var newName by remember { mutableStateOf(name) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = SurfaceCream,
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = DeepMoss) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Farmer Name") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen,
                        focusedLabelColor = SageGreen
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        user?.uid?.let { uid ->
                            firestore.collection("users").document(uid).update("name", newName)
                            name = newName
                        }; showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                ) { Text("Save Changes") }
            }
        )
    }

    // Modern Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = SurfaceCream,
            title = { Text("Confirm Logout", fontWeight = FontWeight.Bold, color = DeepMoss) },
            text = { Text("Are you sure you want to exit your KisanMate session?") },
            confirmButton = {
                Button(
                    onClick = { firebaseAuth.signOut(); showLogoutDialog = false; onLogout() },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) { Text("Logout", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SandBg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Profile Avatar Section
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = SageGreen.copy(0.15f),
                border = BorderStroke(2.dp, SageGreen.copy(0.3f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Person, null, tint = SageGreen, modifier = Modifier.size(60.dp))
                }
            }
            // Edit Floating Badge
            Surface(
                Modifier.size(36.dp).clickable { showEditDialog = true },
                shape = CircleShape,
                color = DeepMoss,
                shadowElevation = 4.dp
            ) {
                Icon(Icons.Rounded.Edit, null, tint = Color.White, modifier = Modifier.padding(8.dp).size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(name, fontSize = 26.sp, fontWeight = FontWeight.Black, color = DeepMoss)
        Text("+91 $phone", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(40.dp))

        // Options List
        Column(
            Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("General Settings", color = SageGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

            ProfileOption(Icons.Rounded.AccountBalance, "Business Profile")
            ProfileOption(Icons.Rounded.Translate, "App Language")
            ProfileOption(Icons.Rounded.SupportAgent, "Help & Support")

            Spacer(modifier = Modifier.height(8.dp))
            Text("Account Actions", color = Terracotta, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

            ProfileOption(
                icon = Icons.Rounded.Logout,
                label = "Sign Out",
                textColor = Terracotta,
                onClick = { showLogoutDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        Text("KisanMate v2.4.0", fontSize = 12.sp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    label: String,
    textColor: Color = DeepMoss,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(20.dp),
        color = SurfaceCream,
        border = BorderStroke(1.dp, Color(0xFFF1F1F1))
    ) {
        Row(
            Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(40.dp).background(textColor.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = textColor, modifier = Modifier.size(20.dp))
            }

            Text(
                label,
                modifier = Modifier.padding(start = 16.dp).weight(1f),
                fontWeight = FontWeight.Bold,
                color = DeepMoss,
                fontSize = 16.sp
            )

            Icon(Icons.Rounded.ChevronRight, null, tint = Color.LightGray)
        }
    }
}