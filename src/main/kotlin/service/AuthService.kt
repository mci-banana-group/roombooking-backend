package edu.mci.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import edu.mci.model.api.request.LoginRequest
import edu.mci.model.api.request.RegistrationRequest
import edu.mci.model.api.response.LoginResponse
import edu.mci.model.api.response.UserResponse
import edu.mci.model.db.toResponse
import edu.mci.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.transaction

class AuthService(
    private val userRepository: UserRepository,
    private val passwordService: PasswordService,
    private val jwtSecret: String,
    private val jwtIssuer: String,
    private val jwtAudience: String
) {
    fun login(request: LoginRequest): LoginResponse? {
        val user = transaction {
            userRepository.findByEmail(request.email)
        } ?: return null

        if (!passwordService.verifyPassword(request.password, user.password)) {
            return null
        }

        val token = JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("userId", user.id.value)
            .sign(Algorithm.HMAC256(jwtSecret))

        return LoginResponse(
            token = token,
            user = transaction { user.toResponse() }
        )
    }

    fun register(request: RegistrationRequest): UserResponse? = transaction {
        if (userRepository.findByEmail(request.email) != null) {
            return@transaction null
        }

        userRepository.create(
            email = request.email,
            password = passwordService.hashPassword(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            permissionLevel = request.permissionLevel,
            role = request.role
        ).toResponse()
    }
}
