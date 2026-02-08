package edu.mci.model.api.response

import kotlinx.serialization.Serializable

@Serializable
data class BuildingDeletionConflictResponse(
    val message: String,
    val roomsCount: Long
)
