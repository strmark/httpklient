package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpExchange
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpKlientOptions
import com.github.lion7.httpklient.HttpRequest

open class DelegatingHttpKlient(private val delegate: HttpKlient) : HttpKlient {
    override val options: HttpKlientOptions = delegate.options
    override fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, errorReader: BodyReader<*>): HttpExchange<T> = delegate.exchange(request, bodyReader, errorReader)
}
