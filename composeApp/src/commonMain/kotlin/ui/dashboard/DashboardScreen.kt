package com.example.kisanmate.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.kisanmate.ExpenseRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

@Composable
fun DashboardScreen(repository: ExpenseRepository) {
    // Firebase References
    val auth = Firebase.auth
    val firestore = Firebase.firestore
    val currentUser = auth.currentUser

    // State for Dynamic Content
    var userName by remember { mutableStateOf("Farmer") }
    var totalIncome by remember { mutableLongStateOf(0L) }
    var totalExpense by remember { mutableLongStateOf(0L) }

    // Fetch User Profile Name
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            try {
                val snapshot = firestore.collection("users").document(user.uid).get()
                userName = snapshot.get<String>("name") ?: "Sujit Ji"
            } catch (e: Exception) {
                userName = "Sujit Ji" // Fallback to your name
            }
        }
    }

    // Main UI Layout
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBF8))
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Pass the dynamic name to Header
            HeaderSection(name = userName)

            Spacer(modifier = Modifier.height(24.dp))

            // Financial Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    label = "Income",
                    amount = "₹$totalIncome",
                    trend = "+0%",
                    bgColor = Color(0xFFE8F5E9),
                    textColor = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Expenses",
                    amount = "₹$totalExpense",
                    trend = "-0%",
                    bgColor = Color(0xFFFFEBEE),
                    textColor = Color(0xFFC62828),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            TotalProfitCard("₹${totalIncome - totalExpense}")

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionCard(
                    icon = Icons.Default.AddCircle,
                    label = "Add Income",
                    borderColor = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
                ActionCard(
                    icon = Icons.Default.RemoveCircle,
                    label = "Add Expense",
                    borderColor = Color(0xFFFFEBEE),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                Text(text = "See All", color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Transactions will be mapped here from Firestore later
        items(3) {
            TransactionItem()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun HeaderSection(name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("👨🏽‍🌾", fontSize = 24.sp)
        }

        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text(text = "Namaste,", color = Color.Gray, fontSize = 14.sp)
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF3E2723))
        }

        IconButton(onClick = {}) {
            Icon(Icons.Default.Notifications, null, tint = Color.DarkGray)
        }
    }
}

// ... (Rest of your StatCard, TotalProfitCard, ActionCard, and TransactionItem functions remain the same)

@Composable
fun StatCard(
    label: String,
    amount: String,
    trend: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier
) {
    Surface(modifier = modifier, shape = RoundedCornerShape(24.dp), color = bgColor) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (label == "Income")
                        Icons.Default.TrendingUp
                    else
                        Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label,
                    modifier = Modifier.padding(start = 4.dp),
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }

            Text(
                text = amount,
                color = textColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Surface(
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = trend,
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun TotalProfitCard(amount: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF5D4037),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TOTAL PROFIT",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = amount,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White)
            }
        }
    }
}

@Composable
fun ActionCard(
    icon: ImageVector,
    label: String,
    borderColor: Color,
    modifier: Modifier
) {
    Surface(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
        color = Color.White
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(borderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (label.contains("Income"))
                        Color(0xFF2E7D32)
                    else
                        Color(0xFFC62828)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TransactionItem() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(Color(0xFFF1F4F9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Agriculture, null, tint = Color(0xFF5D4037))
            }

            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(
                    text = "Fertilizer Purchase",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
                Text(
                    text = "Oct 24, 2023",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "- ₹2,400",
                color = Color(0xFFC62828),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
@Composable
fun ModernBottomNav(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Box(contentAlignment = Alignment.TopCenter) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.height(80.dp)
        ) {
            NavigationBarItem(
                selected = selectedTab == "home",
                onClick = { onTabSelected("home") },
                icon = { Icon(Icons.Default.Home, null) },
                label = { Text("HOME") }
            )
            NavigationBarItem(
                selected = selectedTab == "reports",
                onClick = { onTabSelected("reports") },
                icon = { Icon(Icons.Default.BarChart, null) },
                label = { Text("REPORTS") }
            )

            Spacer(Modifier.width(60.dp))

            NavigationBarItem(
                selected = selectedTab == "khata",
                onClick = { onTabSelected("khata") },
                icon = { Icon(Icons.Default.MenuBook, null) },
                label = { Text("KHATA") }
            )
            NavigationBarItem(
                selected = selectedTab == "profile",
                onClick = { onTabSelected("profile") },
                icon = { Icon(Icons.Default.Person, null) },
                label = { Text("PROFILE") }
            )
        }

        FloatingActionButton(
            onClick = { /* Add action */ },
            containerColor = Color(0xFF4CAF50),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .offset(y = (-28).dp)
                .size(64.dp)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp))
        }
    }
}

