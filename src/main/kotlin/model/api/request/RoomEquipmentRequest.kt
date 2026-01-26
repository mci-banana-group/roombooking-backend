package edu.mci.model.api.request

import kotlinx.serialization.Serializable

@Serializable
data class RoomEquipmentRequest(
    val type: String,
    val quantity: Int,
    val description: String? = null
)
