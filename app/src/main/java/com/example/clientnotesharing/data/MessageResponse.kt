package com.example.clientnotesharing.data
import kotlinx.serialization.Serializable

/*
 * Data class per ricevere correttamente le risposte del server (non si può usare direttamente String)
 */
@Serializable
data class MessageResponse(
    val message: String
)