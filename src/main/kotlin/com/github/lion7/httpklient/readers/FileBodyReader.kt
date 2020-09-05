package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import java.io.InputStream

class FileBodyReader(override val accept: String = "*/*") : BodyReader<ByteArray> {

    override fun read(inputStream: InputStream): ByteArray = inputStream.use { it.readAllBytes() }
}