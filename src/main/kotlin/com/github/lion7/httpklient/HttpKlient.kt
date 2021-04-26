package com.github.lion7.httpklient

import java.net.URI

interface HttpKlient {

    val options: HttpKlientOptions

    @Throws(HttpKlientException::class)
    fun <T> get(
        uri: URI,
        bodyReader: BodyReader<T>,
        headers: HttpHeaders? = null
    ): T = exchange("GET", uri, bodyReader, BodyWriters.empty(), headers)

    @Throws(HttpKlientException::class)
    fun <T> post(
        uri: URI,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter = BodyWriters.empty(),
        headers: HttpHeaders? = null
    ): T = exchange("POST", uri, bodyReader, bodyWriter, headers)

    @Throws(HttpKlientException::class)
    fun <T> put(
        uri: URI,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter = BodyWriters.empty(),
        headers: HttpHeaders? = null
    ): T = exchange("PUT", uri, bodyReader, bodyWriter, headers)

    @Throws(HttpKlientException::class)
    fun <T> delete(
        uri: URI,
        bodyReader: BodyReader<T>,
        headers: HttpHeaders? = null
    ): T = exchange("DELETE", uri, bodyReader, BodyWriters.empty(), headers)

    @Throws(HttpKlientException::class)
    fun <T> exchange(
        method: String,
        uri: URI,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter = BodyWriters.empty(),
        headers: HttpHeaders? = null
    ): T = exchange(HttpRequest(method, uri, headers ?: HttpHeaders(), bodyWriter), bodyReader).response.body

    @Throws(HttpKlientException::class)
    fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, errorReader: BodyReader<*> = options.errorReader): HttpExchange<T>

}
