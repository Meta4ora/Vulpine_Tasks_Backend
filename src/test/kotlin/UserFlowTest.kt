package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Serializable
data class AuthRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(val token: String, val userId: String)

@Serializable
data class NoteRequest(val title: String, val type: String, val parentId: String? = null)

@Serializable
data class NoteResponse(val id: String, val title: String, val type: String)

class UserFlowTest {

    @Test
    fun `full user flow - register, login, create note, get notes, delete note`() = testApplication {
        // Включаем модуль приложения
        application { module() }

        val testEmail = "e2e-${System.currentTimeMillis()}@test.com"
        val testPassword = "testPass123"

        // 1. Регистрация
        val registerResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(AuthRequest(testEmail, testPassword)))
        }
        assertEquals(HttpStatusCode.OK, registerResponse.status)

        // 2. Логин
        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(AuthRequest(testEmail, testPassword)))
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)

        val authResponse = Json.decodeFromString<AuthResponse>(loginResponse.bodyAsText())
        val token = authResponse.token
        assertNotNull(token)

        // 3. Создание заметки
        val createResponse = client.post("/notes") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(Json.encodeToString(NoteRequest("E2E Test Note", "task")))
        }
        assertEquals(HttpStatusCode.OK, createResponse.status)

        val newNote = Json.decodeFromString<NoteResponse>(createResponse.bodyAsText())
        assertTrue(newNote.id.isNotBlank())
        assertEquals("E2E Test Note", newNote.title)

        // 4. Получение списка заметок
        val getResponse = client.get("/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, getResponse.status)

        val notes = Json.decodeFromString<List<NoteResponse>>(getResponse.bodyAsText())
        assertTrue(notes.any { it.id == newNote.id })

        // 5. Удаление заметки
        val deleteResponse = client.delete("/notes/${newNote.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, deleteResponse.status)

        // 6. Проверка, что заметка удалена
        val getAfterDeleteResponse = client.get("/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        val notesAfterDelete = Json.decodeFromString<List<NoteResponse>>(getAfterDeleteResponse.bodyAsText())
        assertTrue(notesAfterDelete.none { it.id == newNote.id })
    }
}