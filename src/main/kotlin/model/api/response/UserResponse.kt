package edu.mci.model.api.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: MciRole?,
    val isAdmin: Boolean,
)

@Serializable
enum class MciRole {
    STUDENT,
    STAFF,
    LECTURER,
}
