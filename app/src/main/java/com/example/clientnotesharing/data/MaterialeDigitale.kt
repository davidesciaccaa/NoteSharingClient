package com.example.clientnotesharing.data
import kotlinx.serialization.Serializable

@Serializable
data class MaterialeDigitale(
    var id: String,
    var annoRiferimento: Int,
    var descrizioneMateriale: String
) {
}