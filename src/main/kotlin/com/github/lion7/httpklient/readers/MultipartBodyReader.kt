package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyReaders
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import com.github.lion7.httpklient.multipart.FilePart
import com.github.lion7.httpklient.multipart.FormFieldPart
import com.github.lion7.httpklient.multipart.Part
import java.io.InputStream

class MultipartBodyReader(override val accept: String = MediaTypes.MULTIPART_FORM_DATA) : BodyReader<List<Part>> {

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): List<Part> = inputStream.use {
        val contentType = headers.getValue("Content-Type").single()
        val boundary = Regex("boundary=\"?([^\"\b]*)\"?").find(contentType)?.groupValues?.get(1)
            ?: throw IllegalStateException("Boundary not found")
        val buffered = inputStream.buffered()
        val mis = MultipartInputStream(buffered, boundary.toByteArray())
        val parts = mutableListOf<Part>()
        while (mis.nextInputStream()) {
            val partHeaders = HeadersReader.read(buffered)
            val contentDisposition = partHeaders.getValue("Content-Disposition").single()
            val name = Regex("name=\"?([^\"\b]*)\"?").find(contentDisposition)?.groupValues?.get(1)
                ?: throw IllegalStateException("Part name not found")
            val filename = Regex("name=\"?([^\"\b]*)\"?").find(contentDisposition)?.groupValues?.get(1)
            if (filename != null) {
                val path = BodyReaders.ofFile().read(statusCode, partHeaders, mis)
                val part = FilePart(name, filename, path)
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