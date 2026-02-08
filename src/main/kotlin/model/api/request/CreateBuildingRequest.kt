package edu.mci.model.api.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateBuildingRequest(
    val name: String,
    val address: String
)
