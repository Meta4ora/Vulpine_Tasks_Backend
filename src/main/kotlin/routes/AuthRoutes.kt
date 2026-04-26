package com.example.routes

import com.example.config.JwtConfig
import com.example.db.repositories.UsersRepository
import com.example.requests.AuthRequest
import com.example.utils.hash
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.authRoutes() {

    val repo = UsersRepository()

    route("/auth") {

        // регистрация
        post("/register") {
            val req = call.receive<AuthRequest>()

            if (req.email.isBlank() || req.password.length < 6) {
                return@post call.respond(HttpStatusCode.BadRequest, "Invalid data")
            }

            val id = UUID.randomUUID().toString()

            repo.create(id, req.email, hash(req.password))

            call.respond(mapOf("id" to id, "email" to req.email))
        }

        // логин
        post("/login") {
            val req = call.receive<AuthRequest>()

            val result = repo.findByEmail(req.email)
                ?: return@post call.respond(HttpStatusCode.Unauthorized)

            val (user, passwordHash) = result

            if (hash(req.password) != passwordHash) {
                return@post call.respond(HttpStatusCode.Unauthorized)
            }

            val token = JwtConfig.generateToken(user.id)

            call.respond(
                mapOf(
                    "token" to token,
                    "userId" to user.id
                )
            )
        }
    }
}
