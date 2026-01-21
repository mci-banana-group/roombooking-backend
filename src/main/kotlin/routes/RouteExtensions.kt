package edu.mci.routes

import edu.mci.model.db.PermissionLevel
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun ApplicationCall.getUserId(): Int {
    val principal = principal<JWTPrincipal>()
        ?: error("Route reached without authentication. Ensure it is wrapped in an authenticate block.")
    return principal.payload.getClaim("userId").asInt()
}

fun ApplicationCall.isAdmin(): Boolean {
    val principal = principal<JWTPrincipal>()
        ?: error("Route reached without authentication. Ensure it is wrapped in an authenticate block.")
    val permissionLevel = principal.payload.getClaim("permissionLevel").asString() ?: return false
    return permissionLevel == PermissionLevel.ADMIN.name
}
