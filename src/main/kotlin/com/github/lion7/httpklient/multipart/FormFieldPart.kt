package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.BodyWriters
import com.github.lion7.httpklient.HttpHeaders

data class FormFieldPart(
    override val name: String,
    val value: String
) : Part {
    override val headers: HttpHeaders = HttpHeaders()
        .contentDisposition("form-data", mapOf("name" to name))
    override val content: BodyWriter = BodyWriters.ofString(value)
}