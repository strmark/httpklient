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
        private val BEGIN_SEPARATOR = ("#".repeat(16) + " BEGIN " + "#".repeat(14) + "\r\n").toByteArray()
        private val REQUEST_SEPARATOR = ("### REQUEST\r\n").toByteArray()
        private val RESPONSE_SEPARATOR = ("\r\n### RESPONSE\r\n").toByteArray()
        private val END_SEPARATOR = ("\r\n" + "#".repeat(16) + " END " + "#".repeat(15) + "\r\n\r\n").toByteArray()
    }

    private val logStream = logFile.outputStream().buffered()

    init {
        Runtime.getRuntime().addShutdownHook(Thread { close() })
    }

    override fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, errorReader: BodyReader<*>): HttpExchange<T> = try {
        logStream.write(BEGIN_SEPARATOR)
        super.exchange(request.copy(bodyWriter = LoggingBodyWriter(request)), LoggingBodyReader(bodyReader), LoggingBodyReader(errorReader))
    } finally {
        logStream.write(END_SEPARATOR)
    }

    override fun close() = logStream.close()

    inner class LoggingBodyWriter(private val request: HttpRequest) : BodyWriter by request.bodyWriter {
        override fun write(outputStream: OutputStream) {
            logStream.write(REQUEST_SEPARATOR)
            request.writeRequestLineAndHeaders(logStream)
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
