package com.example.kisanmate.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ModernBottomNav(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onFabClick: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {

        NavigationBar(
            modifier = Modifier
                .height(80.dp),
            containerColor = Color.White,
            tonalElevation = 8.dp
        ) {

            NavigationBarItem(
                selected = selectedTab == "home",
                onClick = { onTabSelected("home") },
                icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home"
                    )
                },
                label = { Text("HOME") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4CAF50),
                    selectedTextColor = Color(0xFF4CAF50),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = selectedTab == "reports",
                onClick = { onTabSelected("reports") },
                icon = {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = "Reports"
                    )
                },
                label = { Text("REPORTS") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4CAF50),
                    selectedTextColor = Color(0xFF4CAF50),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(48.dp))

            NavigationBarItem(
                selected = selectedTab == "khata",
                onClick = { onTabSelected("khata") },
                icon = {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = "khata"
                    )
                },
                label = { Text("Transaction") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4CAF50),
                    selectedTextColor = Color(0xFF4CAF50),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = selectedTab == "profile",
                onClick = { onTabSelected("profile") },
                icon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile"
                    )
                },
                label = { Text("PROFILE") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4CAF50),
                    selectedTextColor = Color(0xFF4CAF50),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }

        FloatingActionButton(
            onClick = onFabClick,
            containerColor = Color(0xFF4CAF50),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .offset(y = (-28).dp)
                .size(64.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}