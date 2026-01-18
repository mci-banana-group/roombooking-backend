package edu.mci.repository

import edu.mci.model.db.PermissionLevel
import edu.mci.model.db.Role
import edu.mci.model.db.User
import edu.mci.model.db.Users

interface UserRepository {
    fun findById(id: Int): User?
    fun findByEmail(email: String): User?
    fun create(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        permissionLevel: PermissionLevel,
        role: Role?
    ): User
}

class UserRepositoryImpl : UserRepository {
    override fun findById(id: Int): User? = User.findById(id)
    override fun findByEmail(email: String): User? = User.find { Users.email eq email }.firstOrNull()

    override fun create(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        permissionLevel: PermissionLevel,
        role: Role?
    ): User = User.new {
        this.email = email
        this.password = password
        this.firstName = firstName
        this.lastName = lastName
        this.permissionLevel = permissionLevel
        this.role = role
    }
}
