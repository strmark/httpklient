package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
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
import com.github.lion7.httpklient.readers.HttpResponseBodyReader
import java.io.InputStream

class ThrowingErrorHandler<T>(private val errorReader: BodyReader<T>) {

    private val log = System.getLogger(javaClass.name)

    fun handle(request: HttpRequest, statusCode: Int, headers: HttpHeaders, errorStream: InputStream?): Nothing {
        val response = if (errorStream != null) {
            try {
                HttpResponseBodyReader(errorReader).read(statusCode, headers, errorStream)
            } catch (e: Exception) {
                log.log(System.Logger.Level.ERROR, "Failed to read error stream of HTTP request '${request.method} ${request.uri}' with status code '$statusCode'", e)
                HttpResponse(statusCode, headers, null)
            }
        } else {
            HttpResponse(statusCode, headers, null)
        }
        when (statusCode) {
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