package com.example.routes

import com.example.db.repositories.NotesRepository
import com.example.models.Note
import com.example.requests.CreateNoteRequest
import com.example.requests.UpdateNoteRequest
import com.example.services.FileStorageService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*

fun Route.notesRoutes() {

    val repository = NotesRepository()
    val storage = FileStorageService()

    authenticate {
        route("/notes") {

            post {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val userId = principal.payload
                    .getClaim("userId")
                    .asString()
                val request = call.receive<CreateNoteRequest>()

                val id = UUID.randomUUID().toString()
                val path = "storage/user_$userId/note_$id.md"

                val content = if (request.type == "task") {
                    "# ${request.title}\n\n- [ ] Новая задача"
                } else {
                    "# ${request.title}\n\n"
                }

                storage.createFile(path, content)

                val note = Note(
                    id = id,
                    userId = userId,
                    title = request.title,
                    type = request.type,
                    parentIds = request.parentIds,
                    filePath = path,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                repository.create(note)
                call.respond(note)
            }

            get {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val userId = principal.payload.getClaim("userId").asString()
                val parentId = call.request.queryParameters["parentId"]

                val notes = if (parentId == null) {
                    repository.getAllByUser(userId)
                } else {
                    repository.getByUser(userId, parentId)
                }

                call.respond(notes)
            }

            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val userId = principal.payload.getClaim("userId").asString()
                val id = call.parameters["id"] ?: return@get call.respondText("Missing id")

                val note = repository.getById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                if (note.userId != userId) {
                    return@get call.respond(HttpStatusCode.Forbidden)
                }

                call.respond(note)
            }

            get("/{id}/content") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val userId = principal.payload.getClaim("userId").asString()
                val id = call.parameters["id"] ?: return@get call.respondText("Missing id")

                val note = repository.getById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                if (note.userId != userId) {
                    return@get call.respond(HttpStatusCode.Forbidden)
                }

                val content = storage.readFile(note.filePath)
                call.respondText(content)
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized)

                val userId = principal.payload.getClaim("userId").asString()
                val id = call.parameters["id"] ?: return@put call.respondText("Missing id")

                val note = repository.getById(id)
                    ?: return@put call.respond(HttpStatusCode.NotFound)

                if (note.userId != userId) {
                    return@put call.respond(HttpStatusCode.Forbidden)
                }

                val request = call.receive<UpdateNoteRequest>()

                if (request.title != null) {
                    repository.updateTitle(id, request.title)
                }

                if (request.parentIds != null) {
                    repository.updateParentIds(id, request.parentIds)
                }

                call.respondText("Updated")
            }

            put("/{id}/content") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized)

                val userId = principal.payload.getClaim("userId").asString()
                val id = call.parameters["id"] ?: return@put call.respondText("Missing id")

                val note = repository.getById(id)
                    ?: return@put call.respond(HttpStatusCode.NotFound)

                if (note.userId != userId) {
                    return@put call.respond(HttpStatusCode.Forbidden)
                }

                val content = call.receiveText()
                storage.updateFile(note.filePath, content)
                call.respondText("Content updated")
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized)

                val userId = principal.payload.getClaim("userId").asString()
                val id = call.parameters["id"] ?: return@delete call.respondText("Missing id")

                val note = repository.getById(id)
                    ?: return@delete call.respond(HttpStatusCode.NotFound)

                if (note.userId != userId) {
                    return@delete call.respond(HttpStatusCode.Forbidden)
                }

                File(note.filePath).delete()
                repository.delete(id)
                call.respondText("Deleted")
            }
        }
    }
}