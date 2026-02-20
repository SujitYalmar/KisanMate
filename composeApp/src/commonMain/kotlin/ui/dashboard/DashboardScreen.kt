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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F2)) // Soft mint/gray background
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))

            // Welcome Header
            Text(
                text = "Namaste, $userName 👨🏽‍🌾",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1B3D2F)
            )
            Text(text = "Manage your farm finances", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Modern Balance Card
            ModernSummaryCard(totalIncome, totalExpense)

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionButton(
                    label = "Income",
                    icon = Icons.Default.TrendingUp,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f),
                    onClick = { dialogType = "income"; showDialog = true }
                )
                ActionButton(
                    label = "Expense",
                    icon = Icons.Default.TrendingDown,
                    color = Color(0xFFC62828),
                    modifier = Modifier.weight(1f),
                    onClick = { dialogType = "expense"; showDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Recent Transactions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B3D2F)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(transactions.take(10)) { txn ->
            ModernTransactionItem(txn)
            Spacer(modifier = Modifier.height(12.dp))
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun ModernSummaryCard(income: Long, expense: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3D2F)) // Dark Forest Green
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("NET PROFIT", color = Color.White.copy(0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("₹${income - expense}", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.White.copy(0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryMiniBlock("Income", "₹$income", Color(0xFF81C784))
                SummaryMiniBlock("Expense", "₹$expense", Color(0xFFE57373))
            }
        }
    }
}

@Composable
fun SummaryMiniBlock(label: String, amount: String, color: Color) {
    Column {
        Text(label, color = Color.White.copy(0.7f), fontSize = 12.sp)
        Text(amount, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ActionButton(label: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.Bold, color = Color(0xFF1B3D2F))
        }
    }
}

@Composable
fun ModernTransactionItem(transaction: Transaction) {
    val isIncome = transaction.type == "income"

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
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isIncome) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text("Today", color = Color.Gray, fontSize = 12.sp) // You can add date formatting here
            }

            Text(
                text = if (isIncome) "+₹${transaction.amount}" else "-₹${transaction.amount}",
                color = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}