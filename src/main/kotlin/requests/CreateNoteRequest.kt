package com.example.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequest(
    val title: String,
    val type: String,
    val parentIds: List<String> = emptyList()  // Изменено на массив
)