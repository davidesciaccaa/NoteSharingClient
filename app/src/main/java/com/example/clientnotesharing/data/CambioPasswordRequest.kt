package com.example.clientnotesharing.data

import kotlinx.serialization.Serializable

@Serializable
data class CambioPasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val username: String
) {
}
