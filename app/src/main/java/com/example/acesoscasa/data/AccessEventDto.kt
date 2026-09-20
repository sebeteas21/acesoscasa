package com.example.acesoscasa.data

data class AccessEventDto(
    val device_id: String,
    val user_id: String,
    val timestamp: String,
    val status: String = "GRANTED"
)
