package com.example.db.tables

import org.jetbrains.exposed.sql.Table

object NotesTable : Table("notes") {
    val id = varchar("id", 256)
    val userId = varchar("user_id", 256)
    val title = varchar("title", 256)
    val type = varchar("type", 256)
    val parentIds = varchar("parent_ids", 1024).nullable().default("[]")  // Храним "[]" или "[\"id1\",\"id2\"]"
    val filePath = varchar("file_path", 512)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}