package edu.mci.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class BuildingDto(
    val id: Long,
    val name: String,
)
