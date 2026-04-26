package com.example.config

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

object JwtConfig {
    private const val secret = "secret"

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier = JWT.require(algorithm).build()

    fun generateToken(userId: String): String {
        return JWT.create()
            .withClaim("userId", userId)
            .sign(algorithm)
    }
}

fun Application.configureAuth() {

    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}