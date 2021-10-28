package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BadGatewayException
import com.github.lion7.httpklient.BadRequestException
import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.BodyReaders
import com.github.lion7.httpklient.ClientStatusException
import com.github.lion7.httpklient.ConflictException
import com.github.lion7.httpklient.ForbiddenException
import com.github.lion7.httpklient.GatewayTimeoutException
import com.github.lion7.httpklient.HttpExchange
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.HttpKlient
import com.github.lion7.httpklient.HttpRequest
import com.github.lion7.httpklient.HttpResponse
import com.github.lion7.httpklient.HttpVersionNotSupportedException
import com.github.lion7.httpklient.InformationalStatusException
import com.github.lion7.httpklient.InternalServerErrorException
import com.github.lion7.httpklient.MethodNotAllowedException
import com.github.lion7.httpklient.NotAcceptableException
import com.github.lion7.httpklient.NotFoundException
import com.github.lion7.httpklient.NotImplementedException
import com.github.lion7.httpklient.RedirectStatusException
import com.github.lion7.httpklient.ServerStatusException
import com.github.lion7.httpklient.ServiceUnavailableException
import com.github.lion7.httpklient.UnauthorizedException
import com.github.lion7.httpklient.UnknownStatusException
import java.io.BufferedInputStream

abstract class AbstractHttpKlient : HttpKlient {

    override fun <T> exchange(request: HttpRequest, bodyReader: BodyReader<T>, errorReader: BodyReader<*>): HttpExchange<T> {
        val headers = HttpHeaders(options.defaultHeaders)
        headers.host(request.uri)
        headers.connection("close")
        headers.accept(bodyReader.accept)
        request.bodyWriter.contentType.takeUnless(String::isEmpty)?.let { headers.contentType(it) }
        request.bodyWriter.contentLength?.let { headers.contentLength(it) }
        headers.putAll(request.headers)

        val actualRequest = request.copy(headers = headers)
        val actualResponse = exchange(actualRequest) { response ->
            when (response.statusCode) {
                // Successful responses
                in 200..299 -> BodyReaders.ofHttpResponse(bodyReader).read(response)
                else -> {
                    val errorResponse = try {
                        BodyReaders.ofHttpResponse(errorReader).read(response)
                    } catch (e: Exception) {
                        HttpResponse(response.statusCode, response.statusReason, response.headers, null)
                    }
                    throwException(actualRequest, errorResponse)
                }
            }
        }
        return HttpExchange(actualRequest, actualResponse)
    }

    abstract fun <T> exchange(request: HttpRequest, responseHandler: (HttpResponse<BufferedInputStream>) -> HttpResponse<T>): HttpResponse<T>

    private fun throwException(request: HttpRequest, response: HttpResponse<*>): Nothing {
        when (response.statusCode) {
            // Informational responses
            in 100..199 -> throw InformationalStatusException(request, response)
            // Redirects
            in 300..399 -> throw RedirectStatusException(request, response)
            // Client errors
            400 -> throw BadRequestException(request, response)
            401 -> throw UnauthorizedException(request, response)
            403 -> throw ForbiddenException(request, response)
            404 -> throw NotFoundException(request, response)
            405 -> throw MethodNotAllowedException(request, response)
            406 -> throw NotAcceptableException(request, response)
            409 -> throw ConflictException(request, response)
            in 400..499 -> throw ClientStatusException(request, response)
            // Server errors
            500 -> throw InternalServerErrorException(request, response)
            501 -> throw NotImplementedException(request, response)
            502 -> throw BadGatewayException(request, response)
            503 -> throw ServiceUnavailableException(request, response)
            504 -> throw GatewayTimeoutException(request, response)
            505 -> throw HttpVersionNotSupportedException(request, response)
            in 500..599 -> throw ServerStatusException(request, response)
            // Unknown responses
            else -> throw UnknownStatusException(request, response)
        }
    }
}
