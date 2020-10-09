package com.github.lion7.httpklient.writers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.MediaTypes
import java.io.OutputStream

class JsonBodyWriter<T : Any>(
    private val value: T,
    private val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(),
    override val contentType: String = MediaTypes.APPLICATION_JSON_UTF_8
) : BodyWriter {


    override fun write(outputStream: OutputStream) = objectMapper.writeValue(outputStream, value)
}
