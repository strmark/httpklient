package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import com.github.lion7.httpklient.impl.HeadersReader
import com.github.lion7.httpklient.multipart.Part
import java.io.File
import java.io.IOException
import java.io.InputStream

class MultipartBodyReader(override val accept: String = MediaTypes.MULTIPART_FORM_DATA) : BodyReader<List<Part>> {

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): List<Part> = inputStream.use {
        val contentType = headers.getValue("Content-Type").single()
        val boundary = contentType.parameters["boundary"] ?: throw IllegalStateException("Parameter 'boundary' is missing in Content-Type header: '$contentType'")
        val mis = MultipartInputStream(inputStream, boundary.toByteArray())
        val parts = mutableListOf<Part>()
        while (mis.nextInputStream()) {
            parts += readPart(mis)
        }
        return parts
    }

    private fun readPart(inputStream: InputStream): Part {
        val headers = HeadersReader.read(inputStream)
        val contentDisposition = headers.getValue("Content-Disposition").single()
        val contentType = headers["Content-Type"]?.singleOrNull()
        val name = contentDisposition.parameters["name"] ?: throw IllegalStateException("Parameter 'name' is missing in Content-Disposition header: '$contentDisposition'")
        val content = if (contentDisposition.parameters["filename"] == null && (contentType == null || contentType.value == MediaTypes.TEXT_PLAIN)) {
            inputStream.readAllBytes().inputStream()
        } else {
            val file = File.createTempFile("httpklient", ".part")
            try {
                file.outputStream().use { outputStream -> inputStream.transferTo(outputStream) }
            } catch (e: IOException) {
                file.delete()
                throw e
            }
            file.inputStream()
        }
        return Part(name, headers, content)
    }

}