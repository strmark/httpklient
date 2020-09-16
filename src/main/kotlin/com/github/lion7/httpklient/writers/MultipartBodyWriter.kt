package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import com.github.lion7.httpklient.impl.HeadersWriter
import com.github.lion7.httpklient.multipart.Part
import java.io.OutputStream
import java.math.BigInteger
import java.util.Random

class MultipartBodyWriter(private vararg val parts: Part) : BodyWriter {

    companion object {
        private val random = Random()
    }

    private val boundary: String = BigInteger(256, random).toString()
    override val contentType: String = HttpHeaders.ValueWithParameters(MediaTypes.MULTIPART_FORM_DATA, mapOf("boundary" to boundary)).toString()

    override fun write(outputStream: OutputStream) {
        val writer = outputStream.bufferedWriter()
        parts.forEach { part ->
            writer.write("--$boundary\r\n")
            HeadersWriter.write(part.headers, writer)
            writer.flush()
            part.content.use { it.transferTo(outputStream) }
            writer.write("\r\n")
        }
        writer.write("--$boundary--")
        writer.flush()
    }

}