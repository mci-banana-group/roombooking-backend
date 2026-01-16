package edu.mci.model.api.response

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentResponse(
    val id: Int,
    val name: String,
    val quantity: Int,
)
