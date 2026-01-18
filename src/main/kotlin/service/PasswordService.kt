package edu.mci.service

import at.favre.lib.crypto.bcrypt.BCrypt

interface PasswordService {
    fun hashPassword(password: String): String
    fun verifyPassword(password: String, hashed: String): Boolean
}

class BCryptPasswordService(private val cost: Int = 12) : PasswordService {

    override fun hashPassword(password: String): String =
        BCrypt.withDefaults().hashToString(cost, password.toCharArray())

    override fun verifyPassword(password: String, hashed: String): Boolean =
        BCrypt.verifyer().verify(password.toCharArray(), hashed).verified
}