package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

abstract class AbstractRawHttpKlient : HttpKlient {

    abstract fun connect(request: HttpRequest): Pair<OutputStream, InputStream>

    override fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, bodyWriter: BodyWriter?): T {
        request.headers.header("Host", request.uri.host)
        request.headers.header("Connection", "close")

        val (rawOut, rawIn) = connect(request)

        val outputStream = rawOut.buffered()
        val writer = outputStream.writer()
        writer.write("${request.method} ${request.uri.path} HTTP/1.1\r\n")
        HeadersWriter.write(request.headers, writer)
        writer.flush()
        bodyWriter?.write(outputStream)

        // read version part of status line
        val inputStream = rawIn.buffered()
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
            else -> ThrowingErrorHandler(options.errorReader).handle(request, statusCode, responseHeaders, inputStream)
        }
    }
}
