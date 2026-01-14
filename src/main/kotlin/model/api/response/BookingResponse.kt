package edu.mci.model.api.response


import kotlinx.datetime.Instant
import kotlinx.serialization.*

@Serializable
data class BookingResponse(
    val id: Int,
    val user: UserResponse,
    val start: Instant,
    val end: Instant,
    val gracePeriodMin: Int,
)