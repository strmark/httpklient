package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.HttpHeaders
import java.io.InputStream

data class FilePart(
    override val name: String,
    val filename: String,
    override val content: InputStream,
    val contentType: String
) : Part {
    override val headers: HttpHeaders = HttpHeaders()
        .contentDisposition("form-data", mapOf("name" to name, "filename" to filename))
        .contentType(contentType)
}