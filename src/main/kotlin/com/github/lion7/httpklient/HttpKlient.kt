package com.github.lion7.httpklient

import com.github.lion7.httpklient.exception.HttpKlientException
import com.github.lion7.httpklient.impl.UrlConnectionHttpKlient
import com.github.lion7.httpklient.readers.DiscardingBodyReader
import java.net.URI
import java.time.Duration

interface HttpKlient {

    companion object {
        val default: HttpKlient = create {}
        fun create(f: Options.Builder.() -> Unit): HttpKlient = UrlConnectionHttpKlient(Options.Builder().apply(f).build())
    }

    val options: Options

    @Throws(HttpKlientException::class)
    fun <T> get(
            uri: URI,
            bodyReader: BodyReader<T>,
            headers: HttpHeaders? = null
    ): T = exchange("GET", uri, bodyReader, null, headers)

    @Throws(HttpKlientException::class)
    fun <T> post(
            uri: URI,
            bodyReader: BodyReader<T>,
            bodyWriter: BodyWriter? = null,
            headers: HttpHeaders? = null
    ): T = exchange("POST", uri, bodyReader, bodyWriter, headers)

    @Throws(HttpKlientException::class)
    fun <T> put(
            uri: URI,
            bodyReader: BodyReader<T>,
            bodyWriter: BodyWriter? = null,
            headers: HttpHeaders? = null
    ): T = exchange("PUT", uri, bodyReader, bodyWriter, headers)

    @Throws(HttpKlientException::class)
    fun <T> exchange(
            method: String,
            uri: URI,
            bodyReader: BodyReader<T>,
            bodyWriter: BodyWriter? = null,
            headers: HttpHeaders? = null
    ): T = exchange(HttpRequest(method, uri, buildRequestHeaders(headers, bodyReader, bodyWriter)), bodyReader, bodyWriter)

    @Throws(HttpKlientException::class)
    fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, bodyWriter: BodyWriter?): T

    private fun buildRequestHeaders(headers: HttpHeaders?, bodyReader: BodyReader<*>, bodyWriter: BodyWriter?): HttpHeaders {
        val requestHeaders = HttpHeaders(options.defaultHeaders)
        requestHeaders.accept(bodyReader.accept)
        bodyWriter?.contentType?.let(requestHeaders::contentType)
        headers?.let(requestHeaders::putAll)
        return requestHeaders
    }

    data class Options(
            val connectTimeout: Duration,
            val readTimeout: Duration,
            val followRedirects: Boolean,
            val defaultHeaders: HttpHeaders,
            val errorReader: BodyReader<*>
    ) {
        class Builder {
            var connectTimeout: Duration = Duration.ofSeconds(10)
            var readTimeout: Duration = Duration.ofSeconds(10)
            var followRedirects: Boolean = true
            var defaultHeaders: HttpHeaders? = null
            var errorReader: BodyReader<*>? = null
            fun build() = Options(connectTimeout, readTimeout, followRedirects, defaultHeaders ?: HttpHeaders(), errorReader ?: DiscardingBodyReader())
        }
    }
}