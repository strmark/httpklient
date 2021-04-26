package com.github.lion7.httpklient

import com.github.lion7.httpklient.impl.HeadersReader
import com.github.lion7.httpklient.impl.HeadersWriter
import com.github.lion7.httpklient.impl.readLine
import com.github.lion7.httpklient.impl.readUntil
import java.io.BufferedInputStream
import java.io.OutputStream
import java.net.URI
import java.nio.charset.StandardCharsets

data class HttpRequest(
    val method: String,
    val uri: URI,
    val headers: HttpHeaders,
    val bodyWriter: BodyWriter
) {

    companion object {
        fun readFrom(inputStream: BufferedInputStream): HttpRequest {
            // read method part of request line
            val method = inputStream.readUntil(' '.toInt())?.toString(StandardCharsets.UTF_8) ?: throw IllegalStateException("Failed to read HTTP method")

            // read path part of request line
            val path = inputStream.readUntil(' '.toInt())?.toString(StandardCharsets.UTF_8) ?: throw IllegalStateException("Failed to read HTTP path")

            // read last part of request line, which is the version followed by /r/n
            val version = inputStream.readLine() ?: throw IllegalStateException("Failed to read HTTP version")
            if (version != "HTTP/1.1") {
                throw IllegalStateException("Unsupported HTTP version '$version'")
            }

            // read the HTTP headers
            val headers = HeadersReader.read(inputStream)

            return HttpRequest(method, URI(path), headers, BodyWriters.ofInputStream(inputStream))
        }
    }

    fun writeRequestLineAndHeaders(outputStream: OutputStream) {
        val writer = outputStream.writer()
        writer.write("$method ${uri.path}${uri.query?.addPrefix("?") ?: ""}${uri.fragment?.addPrefix("#") ?: ""} HTTP/1.1\r\n")
        HeadersWriter.write(headers, writer)
        writer.flush()
    }

    fun writeTo(outputStream: OutputStream) {
        writeRequestLineAndHeaders(outputStream)
        bodyWriter.write(outputStream)
    }

    private fun String.addPrefix(prefix: String) = prefix + this
}
