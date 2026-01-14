package edu.mci.model.api.response

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentResponse(
    val id: Long,
    val name: String,
    val description: String,
)
