package edu.mci.model.api.response

import kotlinx.serialization.*

@Serializable
data class RoomResponse(
    val roomNumber: Int, // unique
    val name: String,
    val description: String,
    val capacity: Int,
)