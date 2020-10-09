package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.MediaTypes
import java.io.InputStream
import java.io.OutputStream

class InputStreamBodyWriter(
    private val inputStream: InputStream,
    override val contentType: String = MediaTypes.APPLICATION_OCTET_STREAM
) : BodyWriter {

    override fun write(outputStream: OutputStream) {
        inputStream.transferTo(outputStream)
    }
}
