package com.github.lion7.httpklient

import java.net.URI
import java.time.Duration

data class HttpKlientOptions(
    val connectTimeout: Duration,
    val readTimeout: Duration,
    val followRedirects: Boolean,
    val defaultHeaders: HttpHeaders,
    val errorReader: BodyReader<*>,
    val proxy: URI?
) {
    class Builder {
        var connectTimeout: Duration = Duration.ofSeconds(10)
        var readTimeout: Duration = Duration.ofSeconds(10)
        var followRedirects: Boolean = true
        var defaultHeaders: HttpHeaders? = null
        var errorReader: BodyReader<*>? = null
        var proxy: URI? = null
        fun build() = HttpKlientOptions(connectTimeout, readTimeout, followRedirects, defaultHeaders ?: HttpHeaders(), errorReader ?: BodyReaders.discarding(), proxy)
    }
}
