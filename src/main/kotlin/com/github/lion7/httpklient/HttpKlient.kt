package com.github.lion7.httpklient

import com.github.lion7.httpklient.exception.HttpKlientException
import com.github.lion7.httpklient.readers.DiscardingBodyReader
import com.github.lion7.httpklient.writers.EmptyBodyWriter
import java.net.URI
import java.time.Duration

interface HttpKlient {

    val options: Options

    @Throws(HttpKlientException::class)
    fun <T> get(
        uri: URI,
        bodyReader: BodyReader<T>,
        headers: HttpHeaders? = null
    ): T = exchange("GET", uri, bodyReader, EmptyBodyWriter, headers)

    @Throws(HttpKlientException::class)
    fun <T> post(
        uri: URI,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter = EmptyBodyWriter,
        headers: HttpHeaders? = null
    ): T = exchange("POST", uri, bodyReader, bodyWriter, headers)

    @Throws(HttpKlientException::class)
    fun <T> put(
        uri: URI,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter = EmptyBodyWriter,
        headers: HttpHeaders? = null
    ): T = exchange("PUT", uri, bodyReader, bodyWriter, headers)

    @Throws(HttpKlientException::class)
    fun <T> delete(
        uri: URI,
        bodyReader: BodyReader<T>,
        headers: HttpHeaders? = null
    ): T = exchange("DELETE", uri, bodyReader, EmptyBodyWriter, headers)

    @Throws(HttpKlientException::class)
    fun <T> exchange(
        method: String,
        uri: URI,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter = EmptyBodyWriter,
        headers: HttpHeaders? = null
    ): T

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
