package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.Headers

data class FilePart(
    override val name: String,
    val filename: String,
    override val content: BodyWriter
) : Part {
    override val headers: Headers = Headers()
        .contentDisposition("form-data", mapOf("name" to name, "filename" to filename))
        .contentType(content.contentType)
}