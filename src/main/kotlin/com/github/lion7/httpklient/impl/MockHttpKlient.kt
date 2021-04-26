package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpKlientOptions
import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.HttpResponse
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URI

class MockHttpKlient(override val options: HttpKlientOptions) : AbstractRawHttpKlient() {

    private val mockRequests = mutableMapOf<String, Pair<HttpRequest, () -> InputStream>>()
    private val mockResponses = mutableMapOf<String, HttpResponse<BodyWriter>>()

    fun findRequest(method: String, uri: URI): Pair<HttpRequest, InputStream> {
        val request = mockRequests.getValue(key(method, uri))
        return request.first to request.second()
    }

    fun mockResponse(method: String, uri: URI, response: HttpResponse<BodyWriter>) {
        mockResponses[key(method, uri)] = response
    }

    override fun connect(request: HttpRequest): Pair<OutputStream, InputStream> {
        val key = key(request.method, request.uri)
        val response = mockResponses.getValue(key)
        val responseStream = ByteArrayOutputStream().use {
            response.writeStatusLineAndHeaders(it)
            response.body.write(it)
            ByteArrayInputStream(it.toByteArray())
        }
        val requestStream = ByteArrayOutputStream()
        mockRequests[key] = request to { requestStream.toByteArray().inputStream() }
        return requestStream to responseStream
    }

    private fun key(method: String, uri: URI) = "$method $uri"
}
