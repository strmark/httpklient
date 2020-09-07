package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import java.io.File
import java.io.OutputStream

class ByteArrayBodyWriter(
    private val file: File,
    override val contentType: String = "application/octet-stream"
) : BodyWriter {

    override fun write(outputStream: OutputStream) {
        file.inputStream().use { it.copyTo(outputStream) }
    }
}