package com.example.db.tables

import org.jetbrains.exposed.sql.Table

object NotesTable : Table("notes") {

    val id = text("id")
    val userId = text("user_id")

    val title = text("title")
    val type = text("type")

    val parentId = text("parent_id").nullable()

    val filePath = text("file_path")

    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}