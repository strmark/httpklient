package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.HttpResponse
import com.github.lion7.httpklient.MediaTypes
import com.github.lion7.httpklient.readers.MultipartBodyReader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MultipartTest {

    private val boundary = "some-random-boundary"
    private val contentDisposition = "form-data; name=subject; filename=random.txt"
    private val contentType = "text/plain"
    private val data = "Some random data"
    private val message = "--$boundary\r\nContent-Disposition: $contentDisposition\r\nContent-Type: $contentType\r\n\r\n$data\r\n--$boundary--"

    @Test
    fun multipartInputStream() {
        MultipartInputStream(message.byteInputStream(), boundary.toByteArray()).use { mis ->
            assertTrue(mis.nextInputStream())
            val reader = mis.bufferedReader()
            assertEquals("Content-Disposition: $contentDisposition", reader.readLine())
            assertEquals("Content-Type: $contentType", reader.readLine())
            assertEquals("", reader.readLine())
            assertEquals(data, reader.readLine())
            assertEquals(null, reader.readLine())
        }
    }

    @Test
    fun multipartBodyReader() {
        val headers = HttpHeaders().contentType(MediaTypes.MULTIPART_FORM_DATA, mapOf("boundary" to boundary))
        val parts = MultipartBodyReader(MediaTypes.MULTIPART_FORM_DATA).read(HttpResponse(200, "OK", headers, message.byteInputStream()))
        val part = parts.single()
        val contentType = part.headers.getValue("Content-Type").single()
        val contentDisposition = part.headers.getValue("Content-Disposition").single()
        assertEquals(this.contentType, contentType.value)
        assertTrue(contentType.parameters.isEmpty())
        assertEquals("form-data", contentDisposition.value)
        assertEquals(2, contentDisposition.parameters.size)
        assertEquals("subject", contentDisposition.parameters["name"])
        assertEquals("random.txt", contentDisposition.parameters["filename"])
        part.content.use {
            assertEquals(data, it.reader().readText())
        }
    }
}
