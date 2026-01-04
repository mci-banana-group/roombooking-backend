package edu.mci.model.dto

import java.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingDto(
    val start: Instant,
    val end: Instant,
    val description: String,
    val roomNumber: Int,
)