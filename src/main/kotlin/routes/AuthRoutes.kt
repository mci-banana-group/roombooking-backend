package edu.mci.routes

import edu.mci.model.api.request.LoginRequest
import edu.mci.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val loginResponse = authService.login(loginRequest)
            if (loginResponse != null) {
                call.respond(loginResponse)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid email or password")
            }
        }
    }
}