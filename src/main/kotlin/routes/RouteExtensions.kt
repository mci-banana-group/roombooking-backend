package edu.mci.routes

import edu.mci.model.db.PermissionLevel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val AdminAuthorizationPlugin = createRouteScopedPlugin("AdminAuthorizationPlugin") {
    on(AuthenticationChecked) { call ->
        if (!call.isAdmin()) {
            call.respond(HttpStatusCode.Forbidden, "Only admins can access this resource")
        }
    }
}

fun Route.authenticateAdmin(build: Route.() -> Unit): Route {
    val authenticatedRoute = createChild(object : RouteSelector() {
        override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
    })
    authenticatedRoute.install(AdminAuthorizationPlugin)
    authenticatedRoute.build()
    return authenticatedRoute
}

fun ApplicationCall.getUserId(): Int {
    val principal = principal<JWTPrincipal>()
        ?: error("Route reached without authentication. Ensure it is wrapped in an authenticate block.")
    return principal.payload.getClaim("userId").asInt()
}

fun ApplicationCall.isAdmin(): Boolean {
    val principal = principal<JWTPrincipal>() ?: return false
    val permissionLevel = principal.payload.getClaim("permissionLevel").asString() ?: return false
    return permissionLevel == PermissionLevel.ADMIN.name
}
