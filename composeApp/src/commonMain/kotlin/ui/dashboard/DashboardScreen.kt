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


val StitchGreen = Color(0xFF1E8E3E)
val StitchRed = Color(0xFFD93025)
val StitchBg = Color(0xFFF8F9FA)
val StitchSurface = Color(0xFFFFFFFF)


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

    val transactions by repository.getTransactions()
        .collectAsState(initial = emptyList())

    // ✅ SORTED BY LATEST DATE
    val sortedTransactions =
        transactions.sortedByDescending { it.timestamp }

    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }


    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            try {
                val snapshot =
                    firestore.collection("users")
                        .document(user.uid)
                        .get()

                userName =
                    snapshot.get<String>("name")
                        ?: "Farmer"

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
                            timestamp = System.currentTimeMillis() // ✅ STORED TIME
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
            .background(StitchBg)
            .padding(horizontal = 24.dp)
    ) {

        item {

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                Column(modifier = Modifier.weight(1f)) {

                    Text("Namaste,", color = Color.Gray, fontSize = 14.sp)

                    Text(
                        userName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF202124)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = StitchSurface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Notifications,
                            null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            MainBalanceCard(totalIncome, totalExpense)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Management",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3C4043),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))


            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                ActionTile(
                    "Add Income",
                    Icons.Rounded.Add,
                    StitchGreen,
                    Modifier.weight(1f)
                ) {
                    dialogType = "income"
                    showDialog = true
                }


                ActionTile(
                    "Add Expense",
                    Icons.Rounded.Remove,
                    StitchRed,
                    Modifier.weight(1f)
                ) {
                    dialogType = "expense"
                    showDialog = true
                }
            }


            Spacer(modifier = Modifier.height(40.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "Recent Activity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF202124)
                )


                Text(
                    "View All",
                    color = StitchGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

        }


        // ✅ SHOW ONLY LATEST 4 TRANSACTIONS
        items(sortedTransactions.take(4)) { txn ->

            ModernTransactionRow(txn)

            Spacer(modifier = Modifier.height(12.dp))
        }


        item {
            Spacer(modifier = Modifier.height(110.dp))
        }
    }
}



@Composable
fun MainBalanceCard(income: Long, expense: Long) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = StitchSurface,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFE8EAED)
        )
    ) {

        Column(modifier = Modifier.padding(24.dp)) {

            Text(
                "NET PROFIT",
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )


            Text(
                "₹${income - expense}",
                fontSize = 38.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF202124)
            )

            Spacer(modifier = Modifier.height(24.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text("Income", color = Color.Gray, fontSize = 12.sp)
                    Text("₹$income", color = StitchGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(Color(0xFFE8EAED))
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text("Expense", color = Color.Gray, fontSize = 12.sp)
                    Text("₹$expense", color = StitchRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}



@Composable
fun ActionTile(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = color.copy(alpha = 0.08f),
    ) {

        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                shape = CircleShape,
                color = color,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(label, fontWeight = FontWeight.Bold, color = color, fontSize = 13.sp)
        }
    }
}



@Composable
fun ModernTransactionRow(txn: Transaction) {

    val isIncome = txn.type == "income"
    val accentColor = if (isIncome) StitchGreen else StitchRed

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = StitchSurface
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                shape = RoundedCornerShape(12.dp),
                color =
                    if (isIncome)
                        StitchGreen.copy(0.1f)
                    else
                        StitchRed.copy(0.1f),

                modifier = Modifier.size(44.dp)
            ) {

                Box(contentAlignment = Alignment.Center) {

                    Icon(
                        if (isIncome)
                            Icons.Rounded.NorthEast
                        else
                            Icons.Rounded.SouthWest,

                        null,

                        tint =
                            if (isIncome)
                                StitchGreen
                            else
                                StitchRed,

                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))


            Column(Modifier.weight(1f)) {

                Text(
                    txn.title,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202124),
                    fontSize = 15.sp
                )


                // ✅ SHOW DATE + TIME
                Text(
                    formatDateTime(txn.timestamp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }


            Text(
                "${if (isIncome) "+" else "-"}₹${txn.amount}",
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                fontSize = 16.sp
            )
        }
    }
}



fun formatDateTime(timestamp: Long): String {

    val sdf =
        SimpleDateFormat(
            "dd MMM yyyy • hh:mm a",
            Locale.getDefault()
        )

    return sdf.format(Date(timestamp))
}