package com.example.kisanmate.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kisanmate.ExpenseRepository
import androidx.compose.ui.draw.clip
import com.example.kisanmate.model.Transaction

@Composable
fun ReportsScreen(repository: ExpenseRepository) {
    // 1. Collect real data from the repository
    val transactions by repository.getTransactions().collectAsState(initial = emptyList())

    // 2. Logic to calculate totals for categories (Filtered by Expense)
    val expenseTransactions = transactions.filter { it.type == "expense" }
    val totalExpense = expenseTransactions.sumOf { it.amount }.toDouble()

    // Grouping transactions by title to show "Spending Categories"
    val categories = expenseTransactions.groupBy { it.title }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBF8))
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Title & Month Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Financial Reports",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3D2F)
                )

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                        Text(" Feb 2026", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.KeyboardArrowDown, null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Real Data Bar Chart (Simplified representation)
            Text("Monthly Performance", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B3D2F))
            Spacer(modifier = Modifier.height(16.dp))

            // Passing transactions to the chart
            FinancialBarChart(transactions)

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Dynamic Category Distribution
            Text("Spending Categories", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B3D2F))
            Spacer(modifier = Modifier.height(16.dp))

            if (categories.isEmpty()) {
                Text("No expenses recorded yet.", color = Color.Gray, modifier = Modifier.padding(16.dp))
            } else {
                categories.forEach { (name, amount) ->
                    val progress = if (totalExpense > 0) (amount.toFloat() / totalExpense.toFloat()) else 0f
                    CategoryRow(
                        name = name,
                        amount = "₹$amount",
                        progress = progress,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun FinancialBarChart(transactions: List<Transaction>) {
    // Basic visualization of Income vs Expense
    val income = transactions.filter { it.type == "income" }.sumOf { it.amount }.toFloat()
    val expense = transactions.filter { it.type == "expense" }.sumOf { it.amount }.toFloat()
    val maxVal = maxOf(income, expense).coerceAtLeast(1f)

    Surface(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            ChartBar(income / maxVal, "Income", Color(0xFF2E7D32))
            ChartBar(expense / maxVal, "Expense", Color(0xFFC62828))
        }
    }
}

@Composable
fun ChartBar(heightFraction: Float, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .fillMaxHeight(heightFraction.coerceAtLeast(0.05f)) // Min height so bar is visible
                .background(color, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        )
        Text(label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun CategoryRow(name: String, amount: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, fontWeight = FontWeight.Medium, color = Color(0xFF3E2723))
            Text(amount, fontWeight = FontWeight.Bold, color = Color(0xFF1B3D2F))
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFE0E0E0)
        )
    }
}