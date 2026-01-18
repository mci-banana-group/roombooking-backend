package edu.mci.model.api.request

import edu.mci.model.db.PermissionLevel
import edu.mci.model.db.Role
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val role: Role? = null,
    val permissionLevel: PermissionLevel,
)
