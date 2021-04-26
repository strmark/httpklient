package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import java.io.OutputStream

class ByteArrayBodyWriter(private val bytes: ByteArray, override val contentType: String) : BodyWriter {

    override val contentLength: Long = bytes.size.toLong()

    override fun write(outputStream: OutputStream) = outputStream.write(bytes)
}
