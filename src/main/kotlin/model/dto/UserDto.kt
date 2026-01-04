package edu.mci.model.dto

import kotlinx.serialization.*

@Serializable
data class UserDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: MciRole,
)

@Serializable
enum class MciRole {
    STUDENT,
    STAFF,
    LECTURER,
}