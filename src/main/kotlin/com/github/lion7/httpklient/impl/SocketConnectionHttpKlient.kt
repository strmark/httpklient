package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.StandardCharsets
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

        // read version part of status line
        val version = inputStream.readUntil(' '.toInt())?.toString(StandardCharsets.UTF_8)
        if (version != "HTTP/1.1") {
            throw IllegalStateException("Unsupported HTTP version '$version'")
        }

        // read status code part of status line
        val code = inputStream.readUntil(' '.toInt())?.toString(StandardCharsets.UTF_8)
        val statusCode = code?.toIntOrNull() ?: throw IllegalStateException("Unsupported HTTP status code '$code'")

        // read last part of status line, which is the reason followed by /r/n
        inputStream.readLine()

        val responseHeaders = HeadersReader.read(inputStream)
        return when (statusCode) {
            // Successful responses
            in 200..299 -> bodyReader.read(statusCode, responseHeaders, inputStream)
            else -> errorHandler.handle(request, statusCode, responseHeaders, inputStream)
        }
    }
}