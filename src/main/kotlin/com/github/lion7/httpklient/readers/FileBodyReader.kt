package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import java.io.File
import java.io.IOException
import java.io.InputStream

class FileBodyReader(override val accept: String = MediaTypes.ALL, private val extension: String = "tmp") : BodyReader<File> {

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): File = inputStream.use {
        val file = File.createTempFile("httpklient", ".$extension")
        try {
            file.outputStream().use { outputStream -> inputStream.transferTo(outputStream) }
        } catch (e: IOException) {
            file.delete()
            throw e
        }
        file
    }
}