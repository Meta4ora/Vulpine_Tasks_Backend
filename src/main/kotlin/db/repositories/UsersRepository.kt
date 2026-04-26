package com.example.db.repositories

import com.example.db.tables.UsersTable
import com.example.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UsersRepository {

    fun create(id: String, email: String, passwordHash: String) {
        transaction {
            UsersTable.insert {
                it[UsersTable.id] = id
                it[UsersTable.email] = email
                it[UsersTable.passwordHash] = passwordHash
                it[createdAt] = System.currentTimeMillis()
            }
        }
    }

    fun findByEmail(email: String): Pair<User, String>? {
        return transaction {
            UsersTable.select { UsersTable.email eq email }
                .map {
                    val user = User(
                        id = it[UsersTable.id],
                        email = it[UsersTable.email],
                        createdAt = it[UsersTable.createdAt]
                    )
                    user to it[UsersTable.passwordHash]
                }
                .singleOrNull()
        }
    }

    fun findById(id: String): User? {
        return transaction {
            UsersTable.select { UsersTable.id eq id }
                .map {
                    User(
                        id = it[UsersTable.id],
                        email = it[UsersTable.email],
                        createdAt = it[UsersTable.createdAt]
                    )
                }
                .singleOrNull()
        }
    }
}