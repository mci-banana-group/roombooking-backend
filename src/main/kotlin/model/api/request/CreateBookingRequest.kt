package edu.mci.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingRequest(
    val start: Instant,
    val end: Instant,
    val description: String,
    val roomNumber: Int,
)