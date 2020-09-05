package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.ErrorHandler
import com.github.lion7.httpklient.HttpKlient
import java.net.HttpURLConnection
import java.net.URI
import java.time.Duration

object JdkHttpKlient : HttpKlient {

    override fun <T> get(
        uri: URI,
        timeout: Duration,
        headers: Map<String, String>,
        bodyReader: BodyReader<T>,
        errorHandler: ErrorHandler<T>
    ): T =
        exchange("GET", uri, timeout, headers, bodyReader, null, errorHandler)

    override fun <T> post(
        uri: URI,
        timeout: Duration,
        headers: Map<String, String>,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter?,
        errorHandler: ErrorHandler<T>
    ): T = exchange("POST", uri, timeout, headers, bodyReader, bodyWriter, errorHandler)

    override fun <T> exchange(
        method: String,
        uri: URI,
        timeout: Duration,
        headers: Map<String, String>,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter?,
        errorHandler: ErrorHandler<T>
    ): T {
        val connection = uri.toURL().openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.instanceFollowRedirects = true
        connection.connectTimeout = timeout.toMillis().toInt()
        connection.readTimeout = timeout.toMillis().toInt()
        connection.setRequestProperty("Accept", bodyReader.accept)
        if (bodyWriter != null) {
            connection.setRequestProperty("Content-Type", bodyWriter.contentType)
        }
        headers.forEach(connection::setRequestProperty)
        if (bodyWriter != null) {
            connection.doOutput = true
            connection.outputStream.use(bodyWriter::write)
        }
        return when (connection.responseCode) {
            // Successful responses
            in 200..299 -> connection.inputStream.let(bodyReader::read)
            else -> errorHandler.handle(connection)
        }
    }
}