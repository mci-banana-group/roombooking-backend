package edu.mci.service

interface PasswordService {
    fun hashPassword(password: String): String
    fun verifyPassword(password: String, hashed: String): Boolean
}

// Implemented that way to easily replaced it with a hashed pw later
class CleartextPasswordService : PasswordService {
    override fun hashPassword(password: String): String = password
    override fun verifyPassword(password: String, hashed: String): Boolean = password == hashed
}