package com.example.services

import java.io.File

class FileStorageService {

    fun createFile(path: String, content: String) {
        val file = File(path)
        file.parentFile.mkdirs()
        file.writeText(content)
    }

    fun readFile(path: String): String {
        return File(path).readText()
    }

    fun updateFile(path: String, content: String) {
        File(path).writeText(content)
    }
}