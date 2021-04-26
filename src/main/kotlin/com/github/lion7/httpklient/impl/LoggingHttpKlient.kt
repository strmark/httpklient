package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpExchange
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.HttpResponse
import org.apache.commons.io.input.TeeInputStream
import org.apache.commons.io.output.TeeOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class LoggingHttpKlient(logFile: File, delegate: HttpKlient) : DelegatingHttpKlient(delegate), AutoCloseable {

    companion object {
        private val REQUEST_SEPARATOR = ("\r\n" + "#".repeat(32) + "\r\n").toByteArray()
        private val RESPONSE_SEPARATOR = ("\r\n" + "-".repeat(32) + "\r\n").toByteArray()
    }

    private val logStream = logFile.outputStream().buffered()

    override fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, errorReader: BodyReader<*>): HttpExchange<T> =
        super.exchange(request.copy(bodyWriter = LoggingBodyWriter(request)), LoggingBodyReader(bodyReader), LoggingBodyReader(errorReader))

    override fun close() = logStream.close()

    inner class LoggingBodyWriter(private val request: HttpRequest) : BodyWriter by request.bodyWriter {
        override fun write(outputStream: OutputStream) {
            logStream.write(REQUEST_SEPARATOR)
            request.writeRequestLineAndHeaders(outputStream)
            request.bodyWriter.write(TeeOutputStream(outputStream, logStream))
        }
    }

    inner class LoggingBodyReader<T>(private val delegate: BodyReader<T>) : BodyReader<T> by delegate {
        override fun <S : InputStream> read(response: HttpResponse<S>): T {
            logStream.write(RESPONSE_SEPARATOR)
            response.writeStatusLineAndHeaders(logStream)
            return delegate.read(HttpResponse(response.statusCode, response.statusReason, response.headers, TeeInputStream(response.body, logStream)))
        }
    }
}
