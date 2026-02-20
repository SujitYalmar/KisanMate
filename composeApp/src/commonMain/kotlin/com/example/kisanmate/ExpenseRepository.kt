package com.example.kisanmate

import com.example.kisanmate.model.Transaction
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepository {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private fun getUserCollection() = auth.currentUser?.let {
        firestore.collection("users").document(it.uid).collection("transactions")
    }

    suspend fun addTransaction(transaction: Transaction) {
        getUserCollection()?.add(transaction)
    }

    // Real-time updates for the list and totals
    fun getTransactions(): Flow<List<Transaction>> {
        val collection = getUserCollection() ?: throw Exception("User not logged in")
        return collection.snapshots.map { snapshot ->
            snapshot.documents.map { it.data() }
        }
    }
}