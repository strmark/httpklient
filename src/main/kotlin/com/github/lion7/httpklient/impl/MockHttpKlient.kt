package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.HttpResponse
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URI

class MockHttpKlient(override val options: HttpKlient.Options) : AbstractRawHttpKlient() {

    private val mockRequests = mutableMapOf<String, Pair<HttpRequest, () -> InputStream>>()
    private val mockResponses = mutableMapOf<String, HttpResponse<BodyWriter?>>()

    fun findRequest(method: String, uri: URI): Pair<HttpRequest, InputStream> {
        val request = mockRequests.getValue(key(method, uri))
        return request.first to request.second()
    }

    fun mockResponse(method: String, uri: URI, response: HttpResponse<BodyWriter?>) {
        mockResponses[key(method, uri)] = response
    }

    override fun connect(request: HttpRequest): Pair<OutputStream, InputStream> {
        val key = key(request.method, request.uri)
        val response = mockResponses.getValue(key)
        val responseStream = ByteArrayOutputStream().use {
            val writer = it.writer()
            writer.write("HTTP/1.1 ${response.statusCode} Mock Response\r\n")
            HeadersWriter.write(response.headers, writer)
            writer.flush()
            response.body?.write(it)
            ByteArrayInputStream(it.toByteArray())
        }
        val requestStream = ByteArrayOutputStream()
        mockRequests[key] = request to { ByteArrayInputStream(requestStream.toByteArray()) }
        return requestStream to responseStream
    }

    private fun key(method: String, uri: URI) = "$method $uri"
}
