package edu.mci.model.dto

import kotlinx.serialization.*

@Serializable
data class RoomWithBookingsDto(
    val room: RoomDto,
    val bookings: List<BookingDto>,
)
