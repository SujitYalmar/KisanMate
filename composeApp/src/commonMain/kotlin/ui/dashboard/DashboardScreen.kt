package com.example.kisanmate.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.kisanmate.ExpenseRepository
import com.example.kisanmate.model.Transaction
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Custom Palette from HTML Reference
val PrimaryGreen = Color(0xFF269739)
val EarthBrown = Color(0xFF5D4037)
val ExpenseRed = Color(0xFFD32F2F)
val BgLight = Color(0xFFF6F8F6)

@Composable
fun DashboardScreen(
    repository: ExpenseRepository,
    onViewAllClick: () -> Unit
) {
    val auth = Firebase.auth
    val firestore = Firebase.firestore
    val currentUser = auth.currentUser
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("Farmer") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf("income") }

    val transactions by repository.getTransactions().collectAsState(initial = emptyList())
    val sortedTransactions = transactions.sortedByDescending { it.timestamp }

    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            try {
                val snapshot = firestore.collection("users").document(user.uid).get()
                userName = snapshot.get<String>("name") ?: "Farmer"
            } catch (_: Exception) { userName = "Farmer" }
        }
    }

    if (showDialog) {
        AddTransactionDialog(
            type = dialogType,
            onDismiss = { showDialog = false },
            onSave = { title, amount ->
                scope.launch {
                    repository.addTransaction(
                        Transaction(
                            title = title,
                            amount = amount,
                            type = dialogType,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                showDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .padding(horizontal = 24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))

            // Header Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp),
                    color = PrimaryGreen.copy(0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(0.3f))
                ) {
                    Icon(Icons.Default.Person, null, tint = EarthBrown, modifier = Modifier.padding(8.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Namaste,", color = Color.Gray, fontSize = 14.sp)
                    Text(userName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = EarthBrown)
                }
                IconButton(onClick = {}, modifier = Modifier.background(Color.White, CircleShape)) {
                    Icon(Icons.Default.Notifications, null, tint = EarthBrown)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Horizontal Small Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SmallStatCard("Income", "₹$totalIncome", Icons.Rounded.TrendingUp, PrimaryGreen)
                SmallStatCard("Expenses", "₹$totalExpense", Icons.Rounded.TrendingDown, ExpenseRed)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Main Net Profit Card (Earth Brown)
            MainProfitCard(totalIncome - totalExpense)

            Spacer(modifier = Modifier.height(32.dp))

            Text("Quick Actions", fontWeight = FontWeight.ExtraBold, color = EarthBrown, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActionTile("Add Income", Icons.Rounded.AddCircle, PrimaryGreen, Modifier.weight(1f)) {
                    dialogType = "income"; showDialog = true
                }
                ActionTile("Add Expense", Icons.Rounded.RemoveCircle, ExpenseRed, Modifier.weight(1f)) {
                    dialogType = "expense"; showDialog = true
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Activity", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = EarthBrown)
                Text("See All", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { onViewAllClick() })
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(sortedTransactions.take(4)) { txn ->
            ModernTransactionRow(txn)
            Spacer(modifier = Modifier.height(12.dp))
        }

        item { Spacer(modifier = Modifier.height(120.dp)) }
    }
}

@Composable
fun SmallStatCard(label: String, amount: String, icon: ImageVector, color: Color) {
    Surface(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(20.dp),
        color = color.copy(0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(0.2f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(label, color = Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(amount, color = color, fontSize = 22.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun MainProfitCard(profit: Long) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = EarthBrown,
        shadowElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Decorative background icon
            Icon(
                Icons.Rounded.Grass, null,
                tint = Color.White.copy(0.05f),
                modifier = Modifier.size(120.dp).align(Alignment.BottomEnd).offset(x = 20.dp, y = 20.dp)
            )

            Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("TOTAL PROFIT", color = Color.White.copy(0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("₹$profit", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
                }
                Surface(shape = CircleShape, color = PrimaryGreen, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Rounded.AccountBalanceWallet, null, tint = EarthBrown, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

@Composable
fun ActionTile(label: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(0.2f))
    ) {
        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = color.copy(0.15f), modifier = Modifier.size(50.dp)) {
                Icon(icon, null, tint = color, modifier = Modifier.padding(10.dp).size(30.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(label, fontWeight = FontWeight.Bold, color = EarthBrown, fontSize = 15.sp)
        }
    }
}

@Composable
fun ModernTransactionRow(txn: Transaction) {
    val isIncome = txn.type == "income"
    val accentColor = if (isIncome) PrimaryGreen else ExpenseRed

    // category based colors for icons (like HTML)
    val iconBg = when {
        txn.title.contains("Fertilizer", true) -> Color(0xFFE3F2FD)
        txn.title.contains("Sale", true) -> Color(0xFFFFF8E1)
        else -> accentColor.copy(0.1f)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F1F1))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(12.dp), color = iconBg, modifier = Modifier.size(48.dp)) {
                Icon(
                    if (isIncome) Icons.Rounded.Agriculture else Icons.Rounded.LocalShipping,
                    null, tint = if (isIncome) PrimaryGreen else Color.Gray, modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(txn.title, fontWeight = FontWeight.Bold, color = EarthBrown, fontSize = 16.sp)
                Text(formatDateTime(txn.timestamp), color = Color.Gray, fontSize = 12.sp)
            }
            Text("${if (isIncome) "+" else "-"} ₹${txn.amount}", fontWeight = FontWeight.Black, color = accentColor, fontSize = 16.sp)
        }
    }
}

fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}