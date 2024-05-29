package com.example.clientnotesharing.data
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val usernameSession: String,
    val passwordSession: String
)