package edu.mci.model.api.response

import kotlinx.serialization.Serializable

@Serializable
data class RoomDeletionConflictResponse(
    val message: String,
    val blockers: List<RoomDeletionBlocker>
)

@Serializable
data class RoomDeletionBlocker(
    val type: String,
    val count: Int,
    val suggestedAction: String
)
