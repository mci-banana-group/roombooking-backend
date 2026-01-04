package edu.mci.model.dto

import kotlinx.serialization.*

@Serializable
data class RoomDto(
    val roomNumber: Int, // unique
    val name: String,
    val description: String,
    val capacity: Int,
)