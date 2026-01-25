package edu.mci.model.api.request

import edu.mci.model.db.PermissionLevel
import edu.mci.model.db.Role
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRoleRequest(
    val role: Role,
    val permissionLevel: PermissionLevel
)
