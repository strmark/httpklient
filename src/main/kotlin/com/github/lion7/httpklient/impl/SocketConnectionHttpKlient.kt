package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.readers.HeadersReader
import com.github.lion7.httpklient.writers.HeadersWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Scanner

class SocketConnectionHttpKlient(override val options: HttpKlient.Options) : HttpKlient {

    private val errorHandler = ThrowingErrorHandler(options.errorReader)

    override fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, bodyWriter: BodyWriter?): T {
        request.headers.header("Host", request.uri.host)
        request.headers.header("Connection", "close")

        val socket = Socket()
        socket.soTimeout = options.readTimeout.toMillis().toInt()
        socket.connect(InetSocketAddress(request.uri.host, request.uri.port), options.connectTimeout.toMillis().toInt())

        val outputStream = socket.getOutputStream().buffered()
        val writer = outputStream.writer()
        writer.write("${request.method} ${request.uri.path} HTTP/1.1\r\n")
        HeadersWriter.write(request.headers, writer)
        writer.flush()
        bodyWriter?.write(outputStream)

        val inputStream = socket.getInputStream().buffered()
        val scanner = Scanner(inputStream).useDelimiter("\r\n")
        val (version, code, _) = scanner.nextLine().split(' ', limit = 3)
        if (version != "HTTP/1.1") {
            throw IllegalStateException("Unsupported HTTP version '$version'")
        }

        val statusCode = code.toInt()
        val responseHeaders = HeadersReader.read(inputStream)
        return when (statusCode) {
            // Successful responses
            in 200..299 -> bodyReader.read(statusCode, responseHeaders, inputStream)
            else -> errorHandler.handle(request, statusCode, responseHeaders, inputStream)
        }
    }
}