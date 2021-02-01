package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import java.io.InputStream

class HttpResponseBodyReader<T>(private val bodyReader: BodyReader<T>) : BodyReader<HttpResponse<T>> {

    override val accept: String = bodyReader.accept

    override fun <S : InputStream> read(response: HttpResponse<S>): HttpResponse<T> =
        HttpResponse(response.statusCode, response.statusReason, response.headers, bodyReader.read(response))
}
