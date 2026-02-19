package com.example.kisanmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kisanmate.presentation.auth.AuthViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        Firebase.initialize(this)

        setContent {
            val repository = ExpenseRepository()
            val authViewModel = AuthViewModel()

            // Attach activity to ViewModel
            authViewModel.activity = this

            App(
                repository = repository,
                authViewModel = authViewModel
            )
        }
    }
}
