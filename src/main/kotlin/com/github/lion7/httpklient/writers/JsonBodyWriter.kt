package com.github.lion7.httpklient.writers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lion7.httpklient.BodyWriter
import java.io.OutputStream
import org.apache.commons.io.output.CountingOutputStream

class JsonBodyWriter<T : Any>(private val value: T, private val objectMapper: ObjectMapper, override val contentType: String) : BodyWriter {

    override val contentLength: Long = CountingOutputStream(OutputStream.nullOutputStream()).use {
        write(it)
        it.byteCount
    }

    override fun write(outputStream: OutputStream) = objectMapper.writeValue(outputStream, value)
}
