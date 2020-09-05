package com.github.lion7.httpklient

import com.github.lion7.httpklient.errorhandlers.ThrowingErrorHandler
import com.github.lion7.httpklient.impl.JdkHttpKlient
import java.net.URI
import java.time.Duration

interface HttpKlient {

    companion object {
        val instance: HttpKlient = JdkHttpKlient
    }

    fun <T> get(
        uri: URI,
        timeout: Duration,
        headers: Map<String, String>,
        bodyReader: BodyReader<T>,
        errorHandler: ErrorHandler<T> = ThrowingErrorHandler()
    ): T

    fun <T> post(
        uri: URI,
        timeout: Duration,
        headers: Map<String, String>,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter? = null,
        errorHandler: ErrorHandler<T> = ThrowingErrorHandler()
    ): T

    fun <T> exchange(
        method: String,
        uri: URI,
        timeout: Duration,
        headers: Map<String, String>,
        bodyReader: BodyReader<T>,
        bodyWriter: BodyWriter?,
        errorHandler: ErrorHandler<T>
    ): T
}