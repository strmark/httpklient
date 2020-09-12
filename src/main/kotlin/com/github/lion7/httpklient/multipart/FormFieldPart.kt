package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.HttpHeaders
import java.io.InputStream

data class FormFieldPart(
    override val name: String,
    val value: String
) : Part {
    override val headers: HttpHeaders = HttpHeaders()
        .contentDisposition("form-data", mapOf("name" to name))
    override val content: InputStream = value.byteInputStream()
}