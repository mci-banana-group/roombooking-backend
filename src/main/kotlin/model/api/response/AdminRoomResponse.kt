package edu.mci.model.api.response

import kotlinx.serialization.Serializable

@Serializable
data class AdminRoomResponse(
    val id: Int,
    val roomNumber: Int,
    val name: String,
    val description: String,
    val status: String,
    val confirmationCode: String,
    val capacity: Int,
    val buildingId: Int
)
