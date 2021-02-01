package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import com.github.lion7.httpklient.MediaTypes
import java.io.InputStream

class StringBodyReader(override val accept: String = MediaTypes.TEXT) : BodyReader<String> {

    override fun <S : InputStream> read(response: HttpResponse<S>): String =
        response.body.reader().readText()
}
