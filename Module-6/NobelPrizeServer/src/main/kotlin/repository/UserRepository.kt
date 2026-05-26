package com.example.repository

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.db.DatabaseFactory.dbQuery
import com.example.db.Users
import com.example.model.UserResponse
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll

class UserRepository {

    suspend fun findByUsername(username: String): UserRow? = dbQuery {
        Users.selectAll()
            .where { Users.username eq username }
            .limit(1)
            .map { it.toUserRow() }
            .firstOrNull()
    }

    suspend fun findById(id: Int): UserRow? = dbQuery {
        Users.selectAll()
            .where { Users.id eq id }
            .limit(1)
            .map { it.toUserRow() }
            .firstOrNull()
    }

    suspend fun create(username: String, plainPassword: String): UserRow = dbQuery {
        val hash = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray())
        val newId = Users.insertAndGetId {
            it[Users.username] = username
            it[Users.passwordHash] = hash
        }.value
        Users.selectAll()
            .where { Users.id eq newId }
            .limit(1)
            .map { it.toUserRow() }
            .first()
    }

    fun verifyPassword(plainPassword: String, hash: String): Boolean =
        BCrypt.verifyer().verify(plainPassword.toCharArray(), hash.toCharArray()).verified

    private fun ResultRow.toUserRow(): UserRow = UserRow(
        id = this[Users.id].value,
        username = this[Users.username],
        passwordHash = this[Users.passwordHash],
        createdAt = this[Users.createdAt].toString(),
    )
}

data class UserRow(
    val id: Int,
    val username: String,
    val passwordHash: String,
    val createdAt: String,
) {
    fun toResponse(): UserResponse = UserResponse(id = id, username = username, createdAt = createdAt)
}
