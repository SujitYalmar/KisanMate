package com.example.kisanmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize //

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 1. Initialize Firebase for the Android platform
        // This is critical for Phone Auth to work on your Vivo device
        Firebase.initialize(this)

        setContent {
            // Keep your existing database setup
//            val databaseDriverFactory = DatabaseDriverFactory()
            val repository = ExpenseRepository()
            App(repository = repository)
        }
    }
}