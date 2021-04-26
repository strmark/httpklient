package com.github.lion7.httpklient

import com.github.lion7.httpklient.impl.HeadersReader
import com.github.lion7.httpklient.impl.HeadersWriter
import com.github.lion7.httpklient.impl.readLine
import com.github.lion7.httpklient.impl.readUntil
import java.io.BufferedInputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

data class HttpResponse<T>(
    val statusCode: Int,
    val statusReason: String,
    val headers: HttpHeaders,
    val body: T
) {

    companion object {
        fun readFrom(inputStream: BufferedInputStream): HttpResponse<BufferedInputStream> {
            // read version part of status line
            val version = inputStream.readUntil(' '.toInt())?.toString(StandardCharsets.UTF_8) ?: throw IllegalStateException("Failed to read HTTP version")
            if (version != "HTTP/1.1") {
                throw IllegalStateException("Unsupported HTTP version '$version'")
            }

            // read status code part of status line
            val statusCode = inputStream.readUntil(' '.toInt())?.toString(StandardCharsets.UTF_8)?.toIntOrNull() ?: throw IllegalStateException("Failed to read HTTP status code")

            // read last part of status line, which is the reason followed by /r/n
            val statusReason = inputStream.readLine() ?: throw IllegalStateException("Failed to read HTTP status reason")

            // read the HTTP headers
            val headers = HeadersReader.read(inputStream)

            return HttpResponse(statusCode, statusReason, headers, inputStream)
        }
    }

    fun writeStatusLineAndHeaders(outputStream: OutputStream) {
        val writer = outputStream.writer()
        writer.write("HTTP/1.1 $statusCode $statusReason\r\n")
        HeadersWriter.write(headers, writer)
        writer.flush()
    }
}
