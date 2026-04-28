package com.example.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateNoteRequest(
    val title: String? = null,
    val parentIds: List<String>? = null  // Изменено на массив
)