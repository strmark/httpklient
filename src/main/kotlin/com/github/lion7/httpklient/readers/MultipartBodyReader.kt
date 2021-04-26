package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.HttpResponse
import com.github.lion7.httpklient.MediaTypes
import com.github.lion7.httpklient.impl.HeadersReader
import com.github.lion7.httpklient.multipart.MultipartInputStream
import com.github.lion7.httpklient.multipart.Part
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

class MultipartBodyReader(override val accept: String) : BodyReader<List<Part>> {

    override fun <S : InputStream> read(response: HttpResponse<S>): List<Part> {
        val contentType = response.headers.getValue(HttpHeaders.CONTENT_TYPE).single()
        val boundary = contentType.parameters["boundary"] ?: throw IllegalStateException("Parameter 'boundary' is missing in Content-Type header: '$contentType'")
        val mis = MultipartInputStream(response.body, boundary.toByteArray())
        val parts = mutableListOf<Part>()
        while (mis.nextInputStream()) {
            parts += readPart(mis.buffered())
        }
        return parts
    }

    private fun readPart(inputStream: BufferedInputStream): Part {
        val headers = HeadersReader.read(inputStream)
        val contentDisposition = headers.getValue(HttpHeaders.CONTENT_DISPOSITION).single()
        val contentType = headers[HttpHeaders.CONTENT_TYPE]?.singleOrNull()
        val name = contentDisposition.parameters["name"] ?: throw IllegalStateException("Parameter 'name' is missing in Content-Disposition header: '$contentDisposition'")
        val content = if (contentDisposition.parameters["filename"] == null && (contentType == null || contentType.value == MediaTypes.TEXT_PLAIN)) {
            inputStream.readAllBytes().inputStream()
        } else {
            val file = File.createTempFile(javaClass.simpleName, ".part")
            file.deleteOnExit()
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
