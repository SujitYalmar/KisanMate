package com.example.kisanmate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform