package com.example.config

import org.jetbrains.exposed.sql.Database

object DatabaseFactory {

    fun init() {
        Database.connect(
            url = "jdbc:postgresql://localhost:5432/vt",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "Fokin25032006"
        )
    }
}