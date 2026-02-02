package edu.mci.model.api.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class AdminUserBookingResponse(
    val id: Int,
    val roomId: Int?,
    val roomName: String?,
    val userId: Int?,
    val startTime: Instant,
    val endTime: Instant,
    val status: String
)
