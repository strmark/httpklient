package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import java.io.InputStream

class StringBodyReader(override val accept: String = "*/*") : BodyReader<String> {

    override fun read(inputStream: InputStream): String = inputStream.use { it.bufferedReader().readText() }
}