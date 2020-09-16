package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.HttpResponse
import java.io.InputStream

class HttpResponseBodyReader<T>(private val bodyReader: BodyReader<T>) : BodyReader<HttpResponse<T>> {

    override val accept: String = bodyReader.accept

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): HttpResponse<T> = HttpResponse(
        statusCode,
        headers,
        bodyReader.read(statusCode, headers, inputStream)
    )
}