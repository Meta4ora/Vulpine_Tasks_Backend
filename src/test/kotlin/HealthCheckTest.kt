package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals

class HealthCheckTest {
    @Test
    fun `server should respond to requests`() = testApplication {
        application { module() }

        val response = client.get("/auth/login")
        // 401 Unauthorized - это нормально, главное что сервер отвечает
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}