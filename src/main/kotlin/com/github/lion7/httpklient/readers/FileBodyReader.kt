package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import com.github.lion7.httpklient.MediaTypes
import java.io.File
import java.io.IOException
import java.io.InputStream

class FileBodyReader(override val accept: String = MediaTypes.ALL, private val extension: String = "tmp") : BodyReader<File> {

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
