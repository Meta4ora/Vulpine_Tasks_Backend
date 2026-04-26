package com.example.db.repositories

import com.example.db.tables.NotesTable
import com.example.models.Note
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class NotesRepository {

    fun create(note: Note) {
        transaction {
            NotesTable.insert {
                it[id] = note.id
                it[userId] = note.userId
                it[title] = note.title
                it[type] = note.type
                it[parentId] = note.parentId
                it[filePath] = note.filePath
                it[createdAt] = note.createdAt
                it[updatedAt] = note.updatedAt
            }
        }
    }

    // НОВЫЙ МЕТОД: получить все заметки пользователя
    fun getAllByUser(userId: String): List<Note> {
        return transaction {
            NotesTable.select { NotesTable.userId eq userId }
                .map {
                    Note(
                        id = it[NotesTable.id],
                        userId = it[NotesTable.userId],
                        title = it[NotesTable.title],
                        type = it[NotesTable.type],
                        parentId = it[NotesTable.parentId],
                        filePath = it[NotesTable.filePath],
                        createdAt = it[NotesTable.createdAt],
                        updatedAt = it[NotesTable.updatedAt]
                    )
                }
        }
    }

    // ИЗМЕНЕННЫЙ МЕТОД: parentId может быть null (означает все заметки)
    // или конкретным значением (только дочерние)
    fun getByUser(userId: String, parentId: String?): List<Note> {
        return transaction {
            val condition = when {
                // Если parentId == "all" или null - возвращаем ВСЕ заметки
                parentId == null || parentId == "all" -> {
                    NotesTable.userId eq userId
                }
                // Если parentId == "root" - только корневые (без родителей)
                parentId == "root" -> {
                    (NotesTable.userId eq userId) and NotesTable.parentId.isNull()
                }
                // Иначе - только дочерние конкретной заметки
                else -> {
                    (NotesTable.userId eq userId) and (NotesTable.parentId eq parentId)
                }
            }

            NotesTable.select { condition }.map {
                Note(
                    id = it[NotesTable.id],
                    userId = it[NotesTable.userId],
                    title = it[NotesTable.title],
                    type = it[NotesTable.type],
                    parentId = it[NotesTable.parentId],
                    filePath = it[NotesTable.filePath],
                    createdAt = it[NotesTable.createdAt],
                    updatedAt = it[NotesTable.updatedAt]
                )
            }
        }
    }

    fun getById(id: String): Note? {
        return transaction {
            NotesTable.select { NotesTable.id eq id }
                .map {
                    Note(
                        id = it[NotesTable.id],
                        userId = it[NotesTable.userId],
                        title = it[NotesTable.title],
                        type = it[NotesTable.type],
                        parentId = it[NotesTable.parentId],
                        filePath = it[NotesTable.filePath],
                        createdAt = it[NotesTable.createdAt],
                        updatedAt = it[NotesTable.updatedAt]
                    )
                }
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

    fun delete(id: String) {
        transaction {
            NotesTable.deleteWhere { NotesTable.id eq id }
        }
    }

    fun updateParentId(id: String, parentId: String?) {
        transaction {
            NotesTable.update({ NotesTable.id eq id }) {
                it[NotesTable.parentId] = parentId
                it[updatedAt] = System.currentTimeMillis()
            }
        }
    }
}