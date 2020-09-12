package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.HttpHeaders
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

data class FilePart(
    override val name: String,
    val filename: String,
    val path: Path,
    val contentType: String = Files.probeContentType(path)
) : Part {
    override val headers: HttpHeaders = HttpHeaders()
        .contentDisposition("form-data", mapOf("name" to name, "filename" to filename))
        .contentType(contentType)
    override val content: InputStream = Files.newInputStream(path)
}