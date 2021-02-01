package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.HttpResponse
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.OutputStream

abstract class AbstractRawHttpKlient : AbstractHttpKlient() {

    abstract fun connect(request: HttpRequest): Pair<OutputStream, InputStream>

    override fun exchange(request: HttpRequest, bodyWriter: BodyWriter): HttpResponse<BufferedInputStream> {
        val (rawOut, rawIn) = connect(request)

        val outputStream = rawOut.buffered()
        request.writeRequestLineAndHeaders(outputStream)
        bodyWriter.write(outputStream)
        outputStream.flush()

        val inputStream = rawIn.buffered()
        return HttpResponse.readStatusLineAndHeaders(inputStream)
    }
}
