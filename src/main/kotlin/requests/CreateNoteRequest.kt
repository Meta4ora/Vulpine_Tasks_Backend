package com.example.requests

import java.util.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequest(
    val title: String,
    val type: String,
    val parentId: String? = null
)