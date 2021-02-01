package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyReaders
import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.HttpResponse
import com.github.lion7.httpklient.exception.BadGatewayException
import com.github.lion7.httpklient.exception.BadRequestException
import com.github.lion7.httpklient.exception.ClientStatusException
import com.github.lion7.httpklient.exception.ConflictException
import com.github.lion7.httpklient.exception.ForbiddenException
import com.github.lion7.httpklient.exception.GatewayTimeoutException
import com.github.lion7.httpklient.exception.HttpVersionNotSupportedException
import com.github.lion7.httpklient.exception.InformationalStatusException
import com.github.lion7.httpklient.exception.InternalServerErrorException
import com.github.lion7.httpklient.exception.MethodNotAllowedException
import com.github.lion7.httpklient.exception.NotAcceptableException
import com.github.lion7.httpklient.exception.NotFoundException
import com.github.lion7.httpklient.exception.NotImplementedException
import com.github.lion7.httpklient.exception.RedirectStatusException
import com.github.lion7.httpklient.exception.ServerStatusException
import com.github.lion7.httpklient.exception.ServiceUnavailableException
import com.github.lion7.httpklient.exception.UnauthorizedException
import com.github.lion7.httpklient.exception.UnknownStatusException
import java.io.BufferedInputStream
import java.net.URI

abstract class AbstractHttpKlient : HttpKlient {

    override fun <T> exchange(method: String, uri: URI, bodyReader: BodyReader<T>, bodyWriter: BodyWriter, headers: HttpHeaders?): T
        = exchange(HttpRequest(method, uri, buildRequestHeaders(uri, headers, bodyReader, bodyWriter)), bodyReader, options.errorReader, bodyWriter)

    open fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, errorReader: BodyReader<*>, bodyWriter: BodyWriter): T {
        val response = exchange(request, bodyWriter)
        when (response.statusCode) {
            // Successful responses
            in 200..299 -> return bodyReader.read(response)
            else -> {
                val errorResponse = if (response.body.isEndOfStream()) {
                    HttpResponse(response.statusCode, response.statusReason, response.headers, null)
                } else {
                    try {
                        BodyReaders.ofHttpResponse(errorReader).read(response)
                    } catch (e: Exception) {
                        HttpResponse(response.statusCode, response.statusReason, response.headers, null)
                    }
                }
                when (response.statusCode) {
                    // Informational responses
                    in 100..199 -> throw InformationalStatusException(request, errorResponse)
                    // Redirects
                    in 300..399 -> throw RedirectStatusException(request, errorResponse)
                    // Client errors
                    400 -> throw BadRequestException(request, errorResponse)
                    401 -> throw UnauthorizedException(request, errorResponse)
                    403 -> throw ForbiddenException(request, errorResponse)
                    404 -> throw NotFoundException(request, errorResponse)
                    405 -> throw MethodNotAllowedException(request, errorResponse)
                    406 -> throw NotAcceptableException(request, errorResponse)
                    409 -> throw ConflictException(request, errorResponse)
                    in 400..499 -> throw ClientStatusException(request, errorResponse)
                    // Server errors
                    500 -> throw InternalServerErrorException(request, errorResponse)
                    501 -> throw NotImplementedException(request, errorResponse)
                    502 -> throw BadGatewayException(request, errorResponse)
                    503 -> throw ServiceUnavailableException(request, errorResponse)
                    504 -> throw GatewayTimeoutException(request, errorResponse)
                    505 -> throw HttpVersionNotSupportedException(request, errorResponse)
                    in 500..599 -> throw ServerStatusException(request, errorResponse)
                    // Unknown responses
                    else -> throw UnknownStatusException(request, errorResponse)
                }
            }
        }
    }

    abstract fun exchange(request: HttpRequest, bodyWriter: BodyWriter): HttpResponse<BufferedInputStream>

    private fun buildRequestHeaders(uri: URI, headers: HttpHeaders?, bodyReader: BodyReader<*>, bodyWriter: BodyWriter): HttpHeaders {
        val requestHeaders = HttpHeaders(options.defaultHeaders)
        requestHeaders.host(uri)
        requestHeaders.connection("close")
        requestHeaders.accept(bodyReader.accept)
        if (bodyWriter.contentType.isNotEmpty()) {
            requestHeaders.contentType(bodyWriter.contentType)
        }
        headers?.let(requestHeaders::putAll)
        return requestHeaders
    }
}
