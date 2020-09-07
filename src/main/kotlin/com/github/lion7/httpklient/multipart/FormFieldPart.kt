package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.Headers
import com.github.lion7.httpklient.writers.StringBodyWriter

data class FormFieldPart(
    override val name: String,
    val value: String
) : Part {
    override val headers: Headers = Headers()
        .contentDisposition("form-data", mapOf("name" to name))
    override val content: BodyWriter = StringBodyWriter(value)
}