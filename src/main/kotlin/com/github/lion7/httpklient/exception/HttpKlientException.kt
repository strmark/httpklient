package com.github.lion7.httpklient.exception

abstract class HttpKlientException(val statusCode: Int, val responseBody: String?) : RuntimeException(responseBody)