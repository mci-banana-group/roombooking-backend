package edu.mci.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckInRequest(
    val bookingId: Long,
    val code: String,
)
