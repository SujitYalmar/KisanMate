package com.example.kisanmate.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kisanmate.ExpenseRepository
import androidx.compose.ui.draw.clip


@Composable
fun ReportsScreen(repository: ExpenseRepository) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBF8)) // Consistent light green tint
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. Title & Month Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Financial Reports",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
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

            // 2. Summary Chart Placeholder
            Text("Monthly Performance", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
            Spacer(modifier = Modifier.height(16.dp))
            MockBarChart()

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Category Distribution
            Text("Spending Categories", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
            Spacer(modifier = Modifier.height(16.dp))

            CategoryRow("Seeds & Fertilizer", "₹12,000", 0.6f, Color(0xFF4CAF50))
            CategoryRow("Labor Costs", "₹8,500", 0.4f, Color(0xFFFFA000))
            CategoryRow("Equipment Fuel", "₹4,200", 0.2f, Color(0xFFF44336))
            CategoryRow("Electricity", "₹1,800", 0.1f, Color(0xFF2196F3))
        }
    }
}

@Composable
fun MockBarChart() {
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
            // Simulated Bars
            Bar(0.4f, "Oct")
            Bar(0.7f, "Nov")
            Bar(0.5f, "Dec")
            Bar(0.9f, "Jan")
            Bar(0.6f, "Feb")
        }
    }
}

@Composable
fun Bar(heightFraction: Float, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(30.dp)
                .fillMaxHeight(heightFraction)
                .background(Color(0xFF2E7D32), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        )
        Text(label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun CategoryRow(name: String, amount: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, fontWeight = FontWeight.Medium, color = Color(0xFF5D4037))
            Text(amount, fontWeight = FontWeight.Bold)
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