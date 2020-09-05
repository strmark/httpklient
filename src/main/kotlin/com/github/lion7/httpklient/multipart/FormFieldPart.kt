package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.writers.StringBodyWriter

data class FormFieldPart(
    override val name: String,
    val value: String,
    val extraHeaders: Map<String, String> = emptyMap()
) : Part {
    private val contentDisposition: String = "form-data; name=\"$name\""
    override val headers: Map<String, String> = mapOf(
        "Content-Disposition" to contentDisposition
    ) + extraHeaders
    override val content: BodyWriter = StringBodyWriter(value)
}