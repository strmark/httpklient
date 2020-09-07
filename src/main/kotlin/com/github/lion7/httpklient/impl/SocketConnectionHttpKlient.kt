package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.*
import com.github.lion7.httpklient.readers.HeadersReader
import com.github.lion7.httpklient.writers.HeadersWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URI
import java.util.*

class SocketConnectionHttpKlient(private val options: Options) : HttpKlient {

    override fun <T, E> exchange(
        method: String,
        uri: URI,
        headers: Headers,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter?,
        errorHandler: BodyReader<E>?
    ): T = Socket().use { socket ->
        headers.headerIfAbsent("Accept", bodyReader.accept)
        if (bodyWriter != null) {
            headers.headerIfAbsent("Content-Type", bodyWriter.contentType)
        }

        socket.soTimeout = options.readTimeout.toMillis().toInt()
        socket.connect(InetSocketAddress(uri.host, uri.port), options.connectTimeout.toMillis().toInt())

        val outputStream = socket.getOutputStream().buffered()
        val writer = outputStream.writer()
        writer.write("$method ${uri.path} HTTP/1.1\r\n")
        HeadersWriter.write(headers, writer)
        writer.flush()
        bodyWriter?.write(outputStream)

        val inputStream = socket.getInputStream().buffered()
        val scanner = Scanner(inputStream).useDelimiter("\r\n")
        val (version, code, reason) = scanner.nextLine().split(' ', limit = 3)
        if (version != "HTTP/1.1") {
            throw IllegalStateException("Unsupported HTTP version '$version'")
        }

        val statusCode = code.toInt()
        val responseHeaders = HeadersReader.read(inputStream)
        return when (statusCode) {
            // Successful responses
            in 200..299 -> bodyReader.read(statusCode, responseHeaders, inputStream)
            else -> ThrowingErrorHandler(errorHandler).handle(statusCode, responseHeaders, inputStream)
        }
    }
}