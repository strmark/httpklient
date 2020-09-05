package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import java.io.OutputStream

class FileBodyWriter(private val bytes: ByteArray, override val contentType: String = "application/octet-stream") :
    BodyWriter {

    override fun write(outputStream: OutputStream) = outputStream.write(bytes)
}