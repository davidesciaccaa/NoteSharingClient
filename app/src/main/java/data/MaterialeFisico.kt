package data

import kotlinx.serialization.Serializable

@Serializable
data class MaterialeFisico(
    var costo: Int,
    var annoRiferimento: Int,
    var nomeCorso: String,
    var descrizioneMateriale: String,
    var comune: String,
    var provincia: String,
    var via: String,
    var cap: Int
) {

}