package com.example.clientnotesharing.data

import kotlinx.serialization.Serializable

/*
 * Data class per inviare/ricevera al/dal server i dati per il cambio della password
 */
@Serializable
data class CambioPasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val username: String
) {
}
