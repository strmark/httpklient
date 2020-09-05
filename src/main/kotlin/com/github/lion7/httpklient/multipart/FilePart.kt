package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.BodyWriter

data class FilePart(
    override val name: String,
    val filename: String,
    override val content: BodyWriter,
    val extraHeaders: Map<String, String> = emptyMap()
) : Part {
    private val contentDisposition: String = "form-data; name=\"$name\"; filename=\"$filename\""
    override val headers: Map<String, String> = mapOf(
        "Content-Disposition" to contentDisposition,
        "Content-Type" to content.contentType
    ) + extraHeaders
}