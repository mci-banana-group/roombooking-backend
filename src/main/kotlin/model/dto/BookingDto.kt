package edu.mci.model.dto

import java.time.Instant
import kotlinx.serialization.*

@Serializable
data class BookingDto(
    val id: Int,
    val user: UserDto,
    val start: Instant,
    val end: Instant,
    val gracePeriodMin: Int,
)