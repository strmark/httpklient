package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import java.io.OutputStream

class StringBodyWriter(private val string: String, override val contentType: String = "text/plain") : BodyWriter {

    override fun write(outputStream: OutputStream) = outputStream.write(string.toByteArray())
}