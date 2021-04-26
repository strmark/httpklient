package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import java.io.InputStream

class ByteArrayBodyReader(override val accept: String) : BodyReader<ByteArray> {

    override fun <S : InputStream> read(response: HttpResponse<S>): ByteArray =
        response.body.readAllBytes()
}
