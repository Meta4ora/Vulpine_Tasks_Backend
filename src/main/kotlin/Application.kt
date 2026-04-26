package com.example

import com.example.config.DatabaseFactory
import com.example.config.configureAuth
import com.example.routes.authRoutes
import com.example.routes.notesRoutes
import com.example.routes.userRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.*
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8083) {
        module()
    }.start(wait = true)
}

fun Application.module() {

    install(CallLogging) {
        level = Level.INFO
    }

    install(ContentNegotiation) {
        json()
    }

    DatabaseFactory.init()

    configureAuth()

    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        authRoutes()
        userRoutes()
        notesRoutes()
    }
    configureRouting()
}