package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.Headers
import java.io.File
import java.io.InputStream

class ByteArrayBodyReader(override val accept: String = "*/*") : BodyReader<File> {

    override fun read(statusCode: Int, headers: Headers, inputStream: InputStream): File = inputStream.use {
        val file = File.createTempFile("httpklient", ".http")
        file.deleteOnExit()
        file.outputStream().use { fos -> it.copyTo(fos) }
        file
    }
}