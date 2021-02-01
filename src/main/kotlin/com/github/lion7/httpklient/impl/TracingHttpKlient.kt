package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.HttpResponse
import io.opentracing.Tracer
import io.opentracing.propagation.Format
import io.opentracing.propagation.TextMap
import io.opentracing.tag.Tags
import java.io.BufferedInputStream
import java.net.URI

class TracingHttpKlient constructor(private val tracer: Tracer, private val delegate: AbstractHttpKlient) : AbstractHttpKlient() {

    override val options: HttpKlient.Options = delegate.options

    override fun <T> exchange(method: String, uri: URI, bodyReader: BodyReader<T>, bodyWriter: BodyWriter, headers: HttpHeaders?): T {
        return delegate.exchange(method, uri, bodyReader, bodyWriter, headers)
    }

    override fun exchange(request: HttpRequest, bodyWriter: BodyWriter): HttpResponse<BufferedInputStream> {
        val span = tracer.buildSpan(request.method)
            .withTag(Tags.COMPONENT, "httpklient")
            .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CLIENT)
            .withTag(Tags.HTTP_URL, request.uri.toASCIIString())
            .withTag(Tags.HTTP_METHOD, request.method)
            .start()
        if (request.uri.port != -1) {
            Tags.PEER_PORT.set(span, request.uri.port)
        }
        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, HttpHeadersAdapter(request.headers))
        try {
            tracer.activateSpan(span).use {
                val response = delegate.exchange(request, bodyWriter)
                Tags.HTTP_STATUS.set(span, response.statusCode)
                return response
            }
        } finally {
            span.finish()
        }
    }

    class HttpHeadersAdapter(private val httpHeaders: HttpHeaders) : TextMap {

        override fun put(key: String, value: String) {
            httpHeaders.header(key, value)
        }
        override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, String>> = httpHeaders.mapValues { it.value.joinToString() }.toMutableMap().entries.iterator()
    }
}
