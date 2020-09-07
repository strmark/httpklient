package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.Headers
import java.io.InputStream

class FileBodyReader(override val accept: String = "*/*") : BodyReader<ByteArray> {

    override fun read(statusCode: Int, headers: Headers, inputStream: InputStream): ByteArray =
        inputStream.use { it.readAllBytes() }
}