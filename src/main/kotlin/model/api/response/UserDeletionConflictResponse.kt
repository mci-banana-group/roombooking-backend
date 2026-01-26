package edu.mci.model.api.response

import kotlinx.serialization.Serializable

@Serializable
data class UserDeletionConflictResponse(
    val message: String,
    val activeBookingsCount: Int
)
