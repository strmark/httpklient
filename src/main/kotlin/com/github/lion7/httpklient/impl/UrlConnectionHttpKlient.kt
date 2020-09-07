package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.*
import java.net.HttpURLConnection
import java.net.URI

class UrlConnectionHttpKlient(private val options: Options) : HttpKlient {

    override fun <T, E> exchange(
        method: String,
        uri: URI,
        headers: Headers,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter?,
        errorHandler: BodyReader<E>?
    ): T {
        headers.headerIfAbsent("Accept", bodyReader.accept)
        if (bodyWriter != null) {
            headers.headerIfAbsent("Content-Type", bodyWriter.contentType)
        }

        val connection = uri.toURL().openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.connectTimeout = options.connectTimeout.toMillis().toInt()
        connection.readTimeout = options.readTimeout.toMillis().toInt()
        connection.instanceFollowRedirects = options.followRedirects

        headers.mapValues { it.value.joinToString(",") }.forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }
        if (bodyWriter != null) {
            connection.doOutput = true
            connection.outputStream.use(bodyWriter::write)
        }

        val responseHeaders = Headers(connection.headerFields)
        return when (val statusCode = connection.responseCode) {
            // Successful responses
            in 200..299 -> bodyReader.read(statusCode, responseHeaders, connection.inputStream)
            else -> ThrowingErrorHandler(errorHandler).handle(statusCode, responseHeaders, connection.errorStream)
        }
    }
}