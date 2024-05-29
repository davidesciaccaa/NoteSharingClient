package com.example.clientnotesharing.data
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val message: String
)