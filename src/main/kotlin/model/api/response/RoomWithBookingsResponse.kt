package edu.mci.model.api.response

import kotlinx.serialization.*

@Serializable
data class RoomWithBookingsResponse(
    val room: RoomResponse,
    val bookings: List<BookingResponse>,
)
