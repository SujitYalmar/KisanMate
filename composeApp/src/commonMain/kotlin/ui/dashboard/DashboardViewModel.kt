package com.example.kisanmate.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kisanmate.ExpenseRepository
import com.example.kisanmate.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardState(
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Long = 0,
    val totalExpense: Long = 0
)

class DashboardViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            repository.getTransactions().collect { list ->
                val income = list
                    .filter { it.type == "income" }
                    .sumOf { it.amount }

                val expense = list
                    .filter { it.type == "expense" }
                    .sumOf { it.amount }

                _state.value = DashboardState(
                    transactions = list,
                    totalIncome = income,
                    totalExpense = expense
                )
            }
        }
    }

    fun addTransaction(title: String, amount: Long, type: String) {
        viewModelScope.launch {
            repository.addTransaction(
                Transaction(
                    title = title,
                    amount = amount,
                    type = type
                )
            )
        }
    }
}
