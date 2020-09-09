package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import java.io.InputStream

class StringBodyReader(override val accept: String = MediaTypes.TEXT) : BodyReader<String> {

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): String =
        inputStream.use { it.bufferedReader().readText() }
}