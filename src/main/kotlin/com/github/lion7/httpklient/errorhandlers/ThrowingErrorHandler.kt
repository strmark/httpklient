package com.github.lion7.httpklient.errorhandlers

import com.github.lion7.httpklient.ErrorHandler
import com.github.lion7.httpklient.exception.*
import com.github.lion7.httpklient.readers.StringBodyReader
import java.net.HttpURLConnection

class ThrowingErrorHandler<T> : ErrorHandler<T> {

    private val stringBodyReader = StringBodyReader()

    override fun handle(connection: HttpURLConnection): Nothing {
        val message = connection.errorStream?.let(stringBodyReader::read) ?: connection.responseMessage
        when (val statusCode = connection.responseCode) {
            // Informational responses
            in 100..199 -> throw InformationalStatusException(statusCode, message)
            // Redirects
            in 300..399 -> throw RedirectStatusException(statusCode, message)
            // Client errors
            in 400..499 -> throw ClientStatusException(statusCode, message)
            // Server errors
            in 500..599 -> throw ServerStatusException(statusCode, message)
            // Unknown responses
            else -> throw UnknownStatusException(statusCode, message)
        }
    }
}