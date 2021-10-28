package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.HttpResponse
import java.io.BufferedInputStream
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

abstract class AbstractRawHttpKlient : AbstractHttpKlient() {

    data class ConnectionInfo(val outputStream: OutputStream, val inputStream: InputStream, val closeable: Closeable) : Closeable by closeable

    abstract fun connect(request: HttpRequest): ConnectionInfo

    override fun <T> exchange(request: HttpRequest, responseHandler: (HttpResponse<BufferedInputStream>) -> HttpResponse<T>): HttpResponse<T> =
        connect(request).use { (outputStream, inputStream, _) ->
            request.writeTo(outputStream)
            responseHandler(HttpResponse.readFrom(inputStream.buffered()))
        }
}
