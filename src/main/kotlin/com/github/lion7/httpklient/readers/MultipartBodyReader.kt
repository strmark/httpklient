package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyReaders
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import com.github.lion7.httpklient.multipart.FilePart
import com.github.lion7.httpklient.multipart.FormFieldPart
import com.github.lion7.httpklient.multipart.Part
import java.io.InputStream
import java.nio.file.Files

class MultipartBodyReader(override val accept: String = MediaTypes.MULTIPART_FORM_DATA) : BodyReader<List<Part>> {

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): List<Part> = inputStream.use {
        val contentType = headers.getValue("Content-Type").single()
        val boundary = contentType.parameters["boundary"] ?: throw IllegalStateException("Boundary is missing in Content-Type header: '$contentType'")
        val buffered = inputStream.buffered()
        val mis = MultipartInputStream(buffered, boundary.toByteArray())
        val parts = mutableListOf<Part>()
        while (mis.nextInputStream()) {
            val partHeaders = HeadersReader.read(buffered)
            val contentDisposition = partHeaders.getValue("Content-Disposition").single()
            val name = contentDisposition.parameters["name"] ?: throw IllegalStateException("Name is missing in Content-Disposition header: '$contentType'")
            val filename = contentDisposition.parameters["filename"]
            if (filename != null) {
                val path = BodyReaders.ofFile().read(statusCode, partHeaders, mis)
                val partContentType = partHeaders["Content-Type"]?.singleOrNull()?.toString() ?: Files.probeContentType(path)
                val part = FilePart(name, filename, Files.newInputStream(path), partContentType)
                part.headers.putAll(partHeaders)
                parts += part
            } else {
                val value = BodyReaders.ofString().read(statusCode, partHeaders, mis)
                val part = FormFieldPart(name, value)
                part.headers.putAll(partHeaders)
                parts += part
            }
        }
        return parts
    }

}