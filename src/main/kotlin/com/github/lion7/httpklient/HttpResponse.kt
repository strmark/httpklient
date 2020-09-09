package com.github.lion7.httpklient

data class HttpResponse<T>(
        val statusCode: Int,
        val headers: HttpHeaders,
        val body: T
)