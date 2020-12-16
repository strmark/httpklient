package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import io.opentracing.Tracer

class TracingHttpKlient(private val tracer: Tracer, private val delegate: HttpKlient) : HttpKlient {

    override val options: HttpKlient.Options = delegate.options

    override fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, bodyWriter: BodyWriter?): T {
        val span = tracer.buildSpan(request.method)
            .withTag("component", "httpklient")
            .withTag("span.kind", "client")
            .withTag("http.url", request.uri.toASCIIString())
            .start()
        try {
            tracer.activateSpan(span).use { return delegate.exchange(request, bodyReader, bodyWriter) }
        } finally {
            span.finish()
        }
    }

}
