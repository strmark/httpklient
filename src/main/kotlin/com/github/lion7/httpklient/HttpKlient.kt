package com.github.lion7.httpklient

import com.github.lion7.httpklient.exception.HttpKlientException
import com.github.lion7.httpklient.impl.UrlConnectionHttpKlient
import java.net.URI

interface HttpKlient {

    companion object {
        val default: HttpKlient = create(Options())
        fun create(options: Options): HttpKlient = UrlConnectionHttpKlient(options)
    }

    @Throws(HttpKlientException::class)
    fun <T, E> get(
        uri: URI,
        headers: Headers,
        bodyReader: BodyReader<T>,
        errorHandler: BodyReader<E>? = null
    ): T = exchange("GET", uri, headers, bodyReader, null, errorHandler)

    @Throws(HttpKlientException::class)
    fun <T, E> post(
        uri: URI,
        headers: Headers,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter? = null,
        errorHandler: BodyReader<E>? = null
    ): T = exchange("POST", uri, headers, bodyReader, bodyWriter, errorHandler)

    @Throws(HttpKlientException::class)
    fun <T, E> exchange(
        method: String,
        uri: URI,
        headers: Headers,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter? = null,
        errorHandler: BodyReader<E>? = null
    ): T
}