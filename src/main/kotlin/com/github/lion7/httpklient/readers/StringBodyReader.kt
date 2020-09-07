package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.Headers
import java.io.InputStream

class StringBodyReader(override val accept: String = "*/*") : BodyReader<String> {

    override fun read(statusCode: Int, headers: Headers, inputStream: InputStream): String =
        inputStream.use { it.bufferedReader().readText() }
}