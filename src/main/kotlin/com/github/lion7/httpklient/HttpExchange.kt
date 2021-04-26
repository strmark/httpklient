package com.github.lion7.httpklient

data class HttpExchange<T>(val request: HttpRequest, val response: HttpResponse<T>)
