package edu.mci.routes

import edu.mci.model.api.request.LoginRequest
import edu.mci.model.api.request.RegistrationRequest
import edu.mci.model.api.response.LoginResponse
import edu.mci.model.api.response.UserResponse
import edu.mci.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        /**
         * Authenticate a user and receive a JWT token.
         *
         * @tag Auth
         * @body application/json [LoginRequest] The user's credentials.
         * @response 200 application/json [LoginResponse] Successfully authenticated.
         * @response 401 text/plain Invalid email or password.
         */
        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val loginResponse = authService.login(loginRequest)
            if (loginResponse != null) {
                call.respond(loginResponse)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid email or password")
            }
        }

        /**
         * Register a new user.
         *
         * @tag Auth
         * @description Use this to easily create new accounts for now. not meant for production. -- User roles: STUDENT, STAFF, LECTURER. -- PermissionLevels: USER, ADMIN
         * @body application/json [RegistrationRequest] The user's registration details.
         * @response 201 application/json [UserResponse] User successfully registered.
         * @response 400 text/plain Email already exists or invalid data.
         */
        post("/register") {
            val registrationRequest = call.receive<RegistrationRequest>()
            val userResponse = authService.register(registrationRequest)
            if (userResponse != null) {
                call.respond(HttpStatusCode.Created, userResponse)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Email already exists")
            }
        }
    }
}