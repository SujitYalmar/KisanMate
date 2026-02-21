package com.example.kisanmate.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kisanmate.ExpenseRepository
import com.example.kisanmate.model.Transaction
import java.util.*

@Composable
fun ReportsScreen(repository: ExpenseRepository) {
    val transactions by repository.getTransactions().collectAsState(initial = emptyList())
    val calendar = Calendar.getInstance()
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var expanded by remember { mutableStateOf(false) }

    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    // Logic: Filter by selected month and current year
    val monthTransactions = transactions.filter {
        val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
        cal.get(Calendar.MONTH) == selectedMonth && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
    }

    val totalIncome = monthTransactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = monthTransactions.filter { it.type == "expense" }.sumOf { it.amount }

    // Grouping by category (title) for the breakdown list
    val categories = monthTransactions.filter { it.type == "expense" }
        .groupBy { it.title }
        .mapValues { it.value.sumOf { txn -> txn.amount } }
        .toList().sortedByDescending { it.second }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9F7))
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. Header with Responsive Month Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Farm Report", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1B3D2F))
                    Text("Year ${calendar.get(Calendar.YEAR)}", color = Color.Gray, fontSize = 14.sp)
                }

                Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                    Surface(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        tonalElevation = 2.dp,
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CalendarToday, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(monthNames[selectedMonth], fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Icon(if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown, null, tint = Color.Gray)
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(Color.White)
                            .heightIn(max = 300.dp) // Makes it scrollable for smaller screens
                    ) {
                        monthNames.forEachIndexed { index, month ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = month,
                                        fontWeight = if (selectedMonth == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedMonth == index) Color(0xFF2E7D32) else Color.Black
                                    )
                                },
                                leadingIcon = {
                                    if (selectedMonth == index) Icon(Icons.Default.Check, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                },
                                onClick = { selectedMonth = index; expanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Statistics Summary Cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReportStatCard("Net Profit", "₹${totalIncome - totalExpense}", Color(0xFF2E7D32), Modifier.weight(1f))
                ReportStatCard("Total Spent", "₹$totalExpense", Color(0xFFC62828), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Responsive Financial Chart
            FinancialBarChart(monthTransactions)

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Expense Breakdown Section
            Text("Expense Breakdown", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B3D2F))
            Spacer(modifier = Modifier.height(16.dp))

            if (categories.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No records found for this month", color = Color.LightGray)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        categories.forEachIndexed { index, (name, amount) ->
                            CategoryRow(
                                name = name,
                                amount = "₹$amount",
                                progress = if (totalExpense > 0) amount.toFloat() / totalExpense.toFloat() else 0f,
                                color = if (index % 2 == 0) Color(0xFF2E7D32) else Color(0xFF81C784)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ReportStatCard(label: String, amount: String, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(amount, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun FinancialBarChart(transactions: List<Transaction>) {
    val income = transactions.filter { it.type == "income" }.sumOf { it.amount }.toFloat()
    val expense = transactions.filter { it.type == "expense" }.sumOf { it.amount }.toFloat()
    val net = income - expense

    // Normalization logic for scaling bars
    val maxAmount = maxOf(income, expense).coerceAtLeast(1f)
    val chartUpperLimit = maxAmount * 1.25f

    Surface(
        modifier = Modifier.fillMaxWidth().height(280.dp),
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Income vs Expense", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                Surface(
                    color = if (net >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = CircleShape
                ) {
                    Text(
                        text = if (net >= 0) "+ ₹${net.toInt()}" else "- ₹${Math.abs(net).toInt()}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (net >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                ResponsiveBar(
                    fraction = income / chartUpperLimit,
                    label = "Income",
                    amount = "₹${income.toInt()}",
                    color = Color(0xFF2E7D32)
                )

                ResponsiveBar(
                    fraction = expense / chartUpperLimit,
                    label = "Expense",
                    amount = "₹${expense.toInt()}",
                    color = Color(0xFFC62828)
                )
            }
        }
    }
}

@Composable
fun RowScope.ResponsiveBar(
    fraction: Float,
    label: String,
    amount: String,
    color: Color
) {
    Column(
        modifier = Modifier.fillMaxHeight().weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(text = amount, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(65.dp)
                .fillMaxHeight(fraction.coerceIn(0.05f, 1f))
                .background(
                    brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.6f), color)),
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B3D2F))
    }
}

@Composable
fun CategoryRow(name: String, amount: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1B3D2F))
            Text(amount, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
            color = color,
            trackColor = Color(0xFFF0F0F0)
        )
    }
}