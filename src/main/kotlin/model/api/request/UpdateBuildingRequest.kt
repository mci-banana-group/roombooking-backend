package edu.mci.model.api.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateBuildingRequest(
    val name: String,
    val address: String
)
