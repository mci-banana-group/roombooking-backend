package edu.mci.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

enum class Role {
    STUDENT, LECTURER, STAFF
}

enum class PermissionLevel {
    USER, MODERATOR, ADMIN
}

object Users : IntIdTable() {
    val email = varchar("email", 255).uniqueIndex()
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val permissionLevel = enumerationByName("permission_level", 20, PermissionLevel::class)
    val role = enumerationByName("role", 20, Role::class).nullable() // Optional as per UML notes
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var email by Users.email
    var firstName by Users.firstName
    var lastName by Users.lastName
    var permissionLevel by Users.permissionLevel
    var role by Users.role
}
