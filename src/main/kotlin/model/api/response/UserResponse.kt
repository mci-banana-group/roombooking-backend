package edu.mci.model.api.response

import kotlinx.serialization.*

@Serializable
data class UserResponse(
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: MciRole?,
)

@Serializable
enum class MciRole {
    STUDENT,
    STAFF,
    LECTURER,
}