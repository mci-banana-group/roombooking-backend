package edu.mci.repository

import edu.mci.model.db.User

interface UserRepository {
    fun findById(id: Int): User?
}

class UserRepositoryImpl : UserRepository {
    override fun findById(id: Int): User? = User.findById(id)
}
