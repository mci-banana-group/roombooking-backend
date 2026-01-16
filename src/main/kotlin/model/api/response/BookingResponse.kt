package edu.mci.model.api.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class BookingResponse(
    val id: Int,
    val user: UserResponse,
    val start: Instant,
    val end: Instant,
    val gracePeriodMin: Int,
    val status: String,
    val description: String,
)