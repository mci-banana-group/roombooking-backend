package edu.mci.model.api.response

import kotlinx.serialization.*

@Serializable
data class BookingWithRoomResponse(
    val booking: BookingResponse,
    val room: RoomResponse,
)
