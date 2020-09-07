package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.multipart.Part
import java.io.OutputStream
import java.math.BigInteger
import java.util.*

class MultipartBodyWriter(private vararg val parts: Part) : BodyWriter {

    companion object {
        private val random = Random()
    }

    private val boundary: String = BigInteger(256, random).toString()
    override val contentType: String = "multipart/form-data; boundary=\"$boundary\""

    override fun write(outputStream: OutputStream) {
        val writer = outputStream.bufferedWriter()
        parts.forEach { part ->
            writer.write("--$boundary\r\n")
            HeadersWriter.write(part.headers, writer)
            writer.flush()
            part.content.write(outputStream)
            writer.write("\r\n")
        }
        writer.write("--$boundary--")
        writer.flush()
    }

}