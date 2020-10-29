package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import java.io.InputStream
import java.net.HttpURLConnection

class UrlConnectionHttpKlient(configure: HttpKlient.Options.Builder.() -> Unit = {}) : HttpKlient {

    override val options: HttpKlient.Options = HttpKlient.Options.Builder().apply(configure).build()

    private val errorHandler = ThrowingErrorHandler(options.errorReader)

    override fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, bodyWriter: BodyWriter?): T {
        val connection = request.uri.toURL().openConnection() as HttpURLConnection
        connection.requestMethod = request.method
        connection.connectTimeout = options.connectTimeout.toMillis().toInt()
        connection.readTimeout = options.readTimeout.toMillis().toInt()
        connection.instanceFollowRedirects = options.followRedirects

        request.headers.mapValues { it.value.joinToString(",") }.forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }
        if (bodyWriter != null) {
            connection.doOutput = true
            connection.outputStream.use(bodyWriter::write)
        }

        val statusCode = connection.responseCode
        val responseHeaders = HttpHeaders()
        responseHeaders.mergeMultiMap(connection.headerFields.filterKeys { it != null })
        return when (statusCode) {
            // Successful responses
            in 200..299 -> connection.inputStream.buffered().use { bodyReader.read(statusCode, responseHeaders, it) }
            else -> (connection.errorStream ?: InputStream.nullInputStream()).buffered().use { errorHandler.handle(request, statusCode, responseHeaders, it) }
        }
    }
}
