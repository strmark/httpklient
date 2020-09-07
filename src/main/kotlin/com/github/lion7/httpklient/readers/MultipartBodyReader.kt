package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.Headers
import com.github.lion7.httpklient.multipart.Part
import java.io.InputStream
import java.io.OutputStream
import java.math.BigInteger
import java.util.*

class MultipartBodyReader : BodyReader<List<Part>> {

    override val accept: String = "multipart/form-data"

    override fun read(statusCode: Int, headers: Headers, inputStream: InputStream): List<Part> {
        return emptyList()
    }

}