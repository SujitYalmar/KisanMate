package com.example.kisanmate.ui.khata

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kisanmate.ExpenseRepository

@Composable
fun KhataScreen(repository: ExpenseRepository) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBF8))
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Header with Add Customer button
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Digital Khata", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
            IconButton(onClick = {}, modifier = Modifier.background(Color(0xFF2E7D32), RoundedCornerShape(12.dp))) {
                Icon(Icons.Default.PersonAdd, null, tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search Farmer/Vendor") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(4) { index ->
                KhataItem(
                    name = if (index % 2 == 0) "Rahul Kumar" else "Sandeep Patil",
                    amount = if (index % 2 == 0) "₹4,500" else "₹1,200",
                    isDue = index % 2 == 0
                )
            }
        }
    }
}

@Composable
fun KhataItem(name: String, amount: String, isDue: Boolean) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(Color(0xFFF1F4F9), RoundedCornerShape(10.dp)), Alignment.Center) {
                Text(name.take(1), fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
            }
            Column(Modifier.padding(start = 16.dp).weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                Text(if (isDue) "You will give" else "You will get", fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = amount,
                color = if (isDue) Color(0xFFC62828) else Color(0xFF2E7D32),
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}