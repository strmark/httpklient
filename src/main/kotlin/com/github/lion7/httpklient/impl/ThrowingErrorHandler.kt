package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.Headers
import com.github.lion7.httpklient.exception.*
import java.io.InputStream

class ThrowingErrorHandler<T>(private val bodyReader: BodyReader<T>? = null) {

    fun handle(statusCode: Int, headers: Headers, errorStream: InputStream?): Nothing {
        val body = if (bodyReader != null && errorStream != null) {
            bodyReader.read(statusCode, headers, errorStream)
        } else {
            null
        }
        when (statusCode) {
            // Informational responses
            in 100..199 -> throw InformationalStatusException(statusCode, headers, body)
            // Redirects
            in 300..399 -> throw RedirectStatusException(statusCode, headers, body)
            // Client errors
            in 400..499 -> throw ClientStatusException(statusCode, headers, body)
            // Server errors
            in 500..599 -> throw ServerStatusException(statusCode, headers, body)
            // Unknown responses
            else -> throw UnknownStatusException(statusCode, headers, body)
        }
    }
}