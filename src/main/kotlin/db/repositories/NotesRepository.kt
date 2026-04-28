package com.example.db.repositories

import com.example.db.tables.NotesTable
import com.example.models.Note
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class NotesRepository {

    // Конвертация List<String> в JSON строку ["id1","id2"]
    private fun listToJsonString(list: List<String>): String {
        if (list.isEmpty()) return "[]"
        return list.joinToString(separator = "\",\"", prefix = "[\"", postfix = "\"]")
    }

    // Конвертация JSON строки в List<String>
    private fun jsonStringToList(json: String?): List<String> {
        if (json.isNullOrBlank() || json == "[]") return emptyList()
        return json
            .removeSurrounding("[", "]")
            .split(",")
            .map { it.trim().removeSurrounding("\"") }
            .filter { it.isNotEmpty() }
    }

    fun create(note: Note) {
        transaction {
            NotesTable.insert {
                it[id] = note.id
                it[userId] = note.userId
                it[title] = note.title
                it[type] = note.type
                it[parentIds] = listToJsonString(note.parentIds)
                it[filePath] = note.filePath
                it[createdAt] = note.createdAt
                it[updatedAt] = note.updatedAt
            }
        }
    }

    fun getAllByUser(userId: String): List<Note> {
        return transaction {
            NotesTable.select { NotesTable.userId eq userId }
                .map { toNote(it) }
        }
    }

    fun getByUser(userId: String, parentId: String?): List<Note> {
        return transaction {
            val condition = if (parentId == null) {
                NotesTable.userId eq userId
            } else {
                // Ищем JSON строку содержащую parentId
                (NotesTable.userId eq userId) and (NotesTable.parentIds like "%\"$parentId\"%")
            }

            NotesTable.select { condition }.map { toNote(it) }
        }
    }

    fun getById(id: String): Note? {
        return transaction {
            NotesTable.select { NotesTable.id eq id }
                .map { toNote(it) }
                .singleOrNull()
        }
    }

    fun updateTitle(id: String, title: String) {
        transaction {
            NotesTable.update({ NotesTable.id eq id }) {
                it[NotesTable.title] = title
                it[updatedAt] = System.currentTimeMillis()
            }
        }
    }

    fun updateParentIds(id: String, parentIds: List<String>) {
        transaction {
            NotesTable.update({ NotesTable.id eq id }) {
                it[NotesTable.parentIds] = listToJsonString(parentIds)
                it[updatedAt] = System.currentTimeMillis()
            }
        }
    }

    fun addParentId(id: String, parentId: String) {
        transaction {
            val note = getById(id) ?: return@transaction
            val currentParentIds = note.parentIds.toMutableList()
            if (!currentParentIds.contains(parentId)) {
                currentParentIds.add(parentId)
                NotesTable.update({ NotesTable.id eq id }) {
                    it[parentIds] = listToJsonString(currentParentIds)
                    it[updatedAt] = System.currentTimeMillis()
                }
            }
        }
    }

    fun removeParentId(id: String, parentId: String) {
        transaction {
            val note = getById(id) ?: return@transaction
            val currentParentIds = note.parentIds.toMutableList()
            if (currentParentIds.remove(parentId)) {
                NotesTable.update({ NotesTable.id eq id }) {
                    it[parentIds] = listToJsonString(currentParentIds)
                    it[updatedAt] = System.currentTimeMillis()
                }
            }
        }
    }

    fun delete(id: String) {
        transaction {
            NotesTable.deleteWhere { NotesTable.id eq id }
        }
    }

    private fun toNote(row: ResultRow): Note {
        val parentIdsJson = row[NotesTable.parentIds]
        val parentIds = jsonStringToList(parentIdsJson)

        return Note(
            id = row[NotesTable.id],
            userId = row[NotesTable.userId],
            title = row[NotesTable.title],
            type = row[NotesTable.type],
            parentIds = parentIds,
            filePath = row[NotesTable.filePath],
            createdAt = row[NotesTable.createdAt],
            updatedAt = row[NotesTable.updatedAt]
        )
    }
}