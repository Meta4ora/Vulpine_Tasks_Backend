package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String,
    val userId: String,
    val title: String,
    val type: String,
    val parentIds: List<String> = emptyList(),
    val filePath: String,
    val createdAt: Long,
    val updatedAt: Long
)