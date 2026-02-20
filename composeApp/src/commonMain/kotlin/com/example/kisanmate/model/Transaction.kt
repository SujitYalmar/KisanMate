package com.example.kisanmate.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String = "",
    val title: String = "",
    val amount: Long = 0L,
    val type: String = "expense",
    val timestamp: Long = 0L
)