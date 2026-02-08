package edu.mci.model.api.response

import kotlinx.serialization.Serializable

@Serializable
data class BuildingResponse(
    val id: Int,
    val name: String,
    val address: String
)
