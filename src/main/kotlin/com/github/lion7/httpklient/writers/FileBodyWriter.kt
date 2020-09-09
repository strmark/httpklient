package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.MediaTypes
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path

class FileBodyWriter(
        private val path: Path,
        override val contentType: String = MediaTypes.APPLICATION_OCTET_STREAM
) : BodyWriter {

    override fun write(outputStream: OutputStream) {
        Files.copy(path, outputStream)
    }
}