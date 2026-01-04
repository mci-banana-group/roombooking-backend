package edu.mci.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckInDto(
    val bookingId: Long,
    val code: String,
)
