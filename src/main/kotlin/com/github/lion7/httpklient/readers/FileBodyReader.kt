package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class FileBodyReader(override val accept: String = MediaTypes.ALL) : BodyReader<Path> {

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): Path = inputStream.use {
        val path = Files.createTempFile("httpklient", ".http")
        path.toFile().deleteOnExit()
        Files.copy(inputStream, path)
        path
    }
}