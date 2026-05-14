package com.example.config

import org.jetbrains.exposed.sql.Database

object DatabaseFactory {

    fun init() {
        val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://vulpine-db:5432/vulpine_tasks"
        val dbUser = System.getenv("DB_USER") ?: "vulpine_user"
        val dbPassword = System.getenv("DB_PASSWORD") ?: "vulpine_password"
        
        Database.connect(
            url = dbUrl,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )
    }
}
