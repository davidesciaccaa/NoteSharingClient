package com.example.clientnotesharing.data
import kotlinx.serialization.Serializable

/*
 * Data class per inviare al server i dati dell'utente che vuole fare login
 */
@Serializable
data class UserSession(
    val usernameSession: String,
    val passwordSession: String
)