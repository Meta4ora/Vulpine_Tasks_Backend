package com.example.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateNoteRequest(
    val title: String
)