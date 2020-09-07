package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.Headers
import java.io.InputStream

class DiscardingBodyReader(override val accept: String = "*/*") : BodyReader<Unit> {

    override fun read(statusCode: Int, headers: Headers, inputStream: InputStream): Unit = inputStream.close()
}