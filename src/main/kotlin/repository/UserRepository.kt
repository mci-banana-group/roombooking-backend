package edu.mci.repository

import edu.mci.model.db.User
import edu.mci.model.db.Users

interface UserRepository {
    fun findById(id: Int): User?
    fun findByEmail(email: String): User?
}

class UserRepositoryImpl : UserRepository {
    override fun findById(id: Int): User? = User.findById(id)
    override fun findByEmail(email: String): User? = User.find { Users.email eq email }.firstOrNull()
}
