package edu.mci.model.api.response

import kotlinx.serialization.Serializable

@Serializable
data class RoomResponse(
    val id: Int,
    val roomNumber: Int,
    val name: String,
    val description: String,
    val status: String,
    val capacity: Int,
    val equipment: List<EquipmentResponse>,
    val building: BuildingResponse?
)
