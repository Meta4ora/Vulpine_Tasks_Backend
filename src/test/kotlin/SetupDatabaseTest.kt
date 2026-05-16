package com.example

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.db.tables.NotesTable
import com.example.db.tables.UsersTable
import org.junit.Test

class SetupDatabaseTest {
    @Test
    fun `create tables for tests`() {
        val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/vulpine_tasks_test"
        val dbUser = System.getenv("DB_USER") ?: "vulpine_user"
        val dbPassword = System.getenv("DB_PASSWORD") ?: "vulpine_password"

        Database.connect(
            url = dbUrl,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )

        transaction {
            SchemaUtils.create(UsersTable, NotesTable)
        }

        println("Таблицы Users и Notes созданы")
    }
}