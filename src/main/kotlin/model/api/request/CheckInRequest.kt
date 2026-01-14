package edu.mci.model.api.request

import kotlinx.serialization.Serializable

@Serializable
data class CheckInRequest(
    val bookingId: Int,
    val code: String,
)
