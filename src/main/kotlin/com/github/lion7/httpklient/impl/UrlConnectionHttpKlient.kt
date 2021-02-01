package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.HttpResponse
import java.io.BufferedInputStream
import java.net.HttpURLConnection

class UrlConnectionHttpKlient constructor(configure: HttpKlient.Options.Builder.() -> Unit = {}) : AbstractHttpKlient() {

    override val options: HttpKlient.Options = HttpKlient.Options.Builder().apply(configure).build()

    override fun exchange(request: HttpRequest, bodyWriter: BodyWriter): HttpResponse<BufferedInputStream> {
        val connection = request.uri.toURL().openConnection() as HttpURLConnection
        connection.requestMethod = request.method
        connection.connectTimeout = options.connectTimeout.toMillis().toInt()
        connection.readTimeout = options.readTimeout.toMillis().toInt()
        connection.instanceFollowRedirects = options.followRedirects

        request.headers.mapValues { it.value.joinToString(",") }.forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }

        if (bodyWriter.contentType.isNotEmpty()) {
            connection.doOutput = true
            connection.outputStream.use { bodyWriter.write(it) }
        }

        val statusCode = connection.responseCode
        val statusReason = connection.responseMessage ?: ""
        val headerFields: Map<String?, List<String>> = connection.headerFields

        val responseHeaders = HttpHeaders()
        responseHeaders.mergeMultiMap(headerFields.filterKeys { it != null }.mapKeys { it.key as String })
        val inputStream = connection.errorStream ?: connection.inputStream
        return HttpResponse(statusCode, statusReason, responseHeaders, inputStream.buffered())
    }
}
