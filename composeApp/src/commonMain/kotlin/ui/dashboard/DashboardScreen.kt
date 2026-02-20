package com.example.kisanmate.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.kisanmate.model.Transaction
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(repository: ExpenseRepository) {
    val auth = Firebase.auth
    val firestore = Firebase.firestore
    val currentUser = auth.currentUser
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("Farmer") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf("income") }

    val transactions by repository.getTransactions().collectAsState(initial = emptyList())

    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            try {
                val snapshot = firestore.collection("users").document(user.uid).get()
                userName = snapshot.get<String>("name") ?: "Farmer"
            } catch (_: Exception) {
                userName = "Farmer"
            }
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

    // Main Scrollable Area
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9F7)) // Very light green tint
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))

            // Farmer Greeting
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Namaste,", color = Color.Gray, fontSize = 14.sp)
                    Text(userName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                }
                IconButton(onClick = { /* Open Notifications */ }) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF1B5E20))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Premium Profit Card
            MainBalanceCard(totalIncome, totalExpense)

            Spacer(modifier = Modifier.height(24.dp))

            // Grid Actions
            Text("Farm Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionTile(
                    label = "Add Income",
                    icon = Icons.Default.AddCircle,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                ) { dialogType = "income"; showDialog = true }

                ActionTile(
                    label = "Add Expense",
                    icon = Icons.Default.RemoveCircle,
                    color = Color(0xFFC62828),
                    modifier = Modifier.weight(1f)
                ) { dialogType = "expense"; showDialog = true }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Activity", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("View All", color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(transactions.take(10)) { txn ->
            ModernTransactionRow(txn)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Bottom Spacer to prevent overlap with Nav Bar
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun MainBalanceCard(income: Long, expense: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("CURRENT PROFIT", color = Color.White.copy(0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("₹${income - expense}", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FinanceSmallBlock("Income", "₹$income", Icons.Default.TrendingUp, Color(0xFF81C784))
                FinanceSmallBlock("Expenses", "₹$expense", Icons.Default.TrendingDown, Color(0xFFE57373))
            }
        }
    }
}

@Composable
fun FinanceSmallBlock(label: String, amount: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Column {
            Text(label, color = Color.White.copy(0.6f), fontSize = 10.sp)
            Text(amount, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionTile(label: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun ModernTransactionRow(txn: Transaction) {
    val isIncome = txn.type == "income"
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(if (isIncome) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isIncome) Icons.Default.CallMade else Icons.Default.CallReceived,
                    null,
                    tint = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(txn.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text("Farm record", color = Color.Gray, fontSize = 11.sp)
            }
            Text(
                "${if (isIncome) "+" else "-"} ₹${txn.amount}",
                fontWeight = FontWeight.Bold,
                color = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}