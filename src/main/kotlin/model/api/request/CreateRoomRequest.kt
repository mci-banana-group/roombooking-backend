package edu.mci.model.api.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomRequest(
    val roomNumber: Int,
    val name: String,
    val description: String,
    val status: String,
    val confirmationCode: String,
    val capacity: Int,
    val buildingId: Int,
    val equipment: List<RoomEquipmentRequest> = emptyList()
)
