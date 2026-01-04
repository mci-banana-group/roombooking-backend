package edu.mci.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentDto(
    val id: Long,
    val name: String,
    val description: String,
)
