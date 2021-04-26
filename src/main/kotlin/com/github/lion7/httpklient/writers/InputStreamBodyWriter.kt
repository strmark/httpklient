package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import java.io.InputStream
import java.io.OutputStream

class InputStreamBodyWriter(private val inputStream: InputStream, override val contentType: String) : BodyWriter {

    override val contentLength: Long? = null

    override fun write(outputStream: OutputStream) {
        inputStream.transferTo(outputStream)
    }
}
