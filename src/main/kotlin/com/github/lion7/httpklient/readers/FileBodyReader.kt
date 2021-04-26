package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import java.io.File
import java.io.IOException
import java.io.InputStream

class FileBodyReader(override val accept: String, private val extension: String) : BodyReader<File> {

    override fun <S : InputStream> read(response: HttpResponse<S>): File {
        val file = File.createTempFile(javaClass.simpleName, ".$extension")
        file.deleteOnExit()
        try {
            file.outputStream().use { outputStream -> response.body.transferTo(outputStream) }
        } catch (e: IOException) {
            file.delete()
            throw e
        }
        return file
    }
}
