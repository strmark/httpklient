package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import com.github.lion7.httpklient.MediaTypes
import java.io.InputStream

class ByteArrayBodyReader(override val accept: String = MediaTypes.ALL) : BodyReader<ByteArray> {

    override fun <S : InputStream> read(response: HttpResponse<S>): ByteArray =
        response.body.readAllBytes()
}
