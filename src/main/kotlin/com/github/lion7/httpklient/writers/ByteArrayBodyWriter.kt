package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.MediaTypes
import java.io.OutputStream

class ByteArrayBodyWriter(
        private val bytes: ByteArray,
        override val contentType: String = MediaTypes.APPLICATION_OCTET_STREAM
) : BodyWriter {

    override fun write(outputStream: OutputStream) = outputStream.write(bytes)
}