package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.MediaTypes
import java.io.OutputStream

class StringBodyWriter(
    private val string: String,
    override val contentType: String = MediaTypes.TEXT_PLAIN
) : BodyWriter {

    override fun write(outputStream: OutputStream) = outputStream.write(string.toByteArray())
}