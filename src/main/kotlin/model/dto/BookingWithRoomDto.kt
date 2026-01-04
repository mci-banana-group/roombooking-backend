package edu.mci.model.dto

import kotlinx.serialization.*

@Serializable
data class BookingWithRoomDto(
    val booking: BookingDto,
    val room: RoomDto,
)
