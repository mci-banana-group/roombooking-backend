package edu.mci.service

import edu.mci.model.api.request.CreateUserRequest
import edu.mci.model.api.request.UpdateUserRoleRequest
import edu.mci.model.api.response.UserDeletionConflictResponse
import edu.mci.model.api.response.UserResponse
import edu.mci.model.db.toResponse
import edu.mci.repository.BookingRepository
import edu.mci.repository.NotificationRepository
import edu.mci.repository.UserRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction

class AdminUserService(
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository,
    private val notificationRepository: NotificationRepository,
    private val passwordService: PasswordService,
    private val clock: Clock = Clock.System
) {
    fun createUser(request: CreateUserRequest): UserResponse = transaction {
        validateCreateRequest(request)
        if (userRepository.findByEmail(request.email) != null) {
            throw UserConflictException("Email already exists")
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

    fun getAllUsers(): List<UserResponse> = transaction {
        userRepository.findAll().map { it.toResponse() }
    }

    fun updateRole(userId: Int, request: UpdateUserRoleRequest): UserResponse = transaction {
        val user = userRepository.findById(userId) ?: throw UserNotFoundException("User not found")
        userRepository.updateRoleAndPermission(user, request.role, request.permissionLevel).toResponse()
    }

    fun deleteUser(userId: Int) = transaction {
        val user = userRepository.findById(userId) ?: throw UserNotFoundException("User not found")
        val now = clock.now().toLocalDateTime(TimeZone.UTC)
        val activeBookingsCount = bookingRepository.countActiveForUserDeletion(userId, now)
        if (activeBookingsCount > 0) {
            throw UserDeletionBlockedException(
                UserDeletionConflictResponse(
                    message = "User deletion blocked by active bookings",
                    activeBookingsCount = activeBookingsCount
                )
            )
        }

        bookingRepository.clearUserReferences(userId)
        notificationRepository.clearUserReferences(userId)
        userRepository.delete(user)
    }

    private fun validateCreateRequest(request: CreateUserRequest) {
        if (request.email.isBlank()) {
            throw UserValidationException("Email is required")
        }
        if (request.password.isBlank()) {
            throw UserValidationException("Password is required")
        }
        if (request.firstName.isBlank()) {
            throw UserValidationException("First name is required")
        }
        if (request.lastName.isBlank()) {
            throw UserValidationException("Last name is required")
        }
    }
}

class UserNotFoundException(message: String) : RuntimeException(message)

class UserValidationException(message: String) : RuntimeException(message)

class UserConflictException(message: String) : RuntimeException(message)

class UserDeletionBlockedException(val conflict: UserDeletionConflictResponse) : RuntimeException(conflict.message)
