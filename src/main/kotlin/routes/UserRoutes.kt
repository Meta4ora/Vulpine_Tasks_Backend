package com.example.routes

import com.example.db.repositories.UsersRepository
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes() {

    val repo = UsersRepository()

    authenticate {
        get("/users/me") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asString()

            val user = repo.findById(userId)
                ?: return@get call.respondText("Not found")

            call.respond(user)
        }
    }
}