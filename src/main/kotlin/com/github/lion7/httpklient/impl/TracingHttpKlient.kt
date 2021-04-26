package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpExchange
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.HttpKlientException
import io.opentracing.Tracer
import io.opentracing.propagation.Format
import io.opentracing.propagation.TextMap
import io.opentracing.tag.Tags

class TracingHttpKlient(private val tracer: Tracer, delegate: HttpKlient) : DelegatingHttpKlient(delegate) {

    override fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, errorReader: BodyReader<*>): HttpExchange<T> {
        val span = tracer.buildSpan("${request.method} ${request.uri.path}")
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
                try {
                    val exchange = super.exchange(request, bodyReader, errorReader)
                    Tags.HTTP_STATUS.set(span, exchange.response.statusCode)
                    return exchange
                } catch (e: HttpKlientException) {
                    Tags.HTTP_STATUS.set(span, e.response.statusCode)
                    Tags.ERROR.set(span, true)
                    throw e
                }
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
