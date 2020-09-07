package com.github.lion7.httpklient.exception

import com.github.lion7.httpklient.Headers

abstract class HttpKlientException(val statusCode: Int, val headers: Headers, val body: Any?) :
    RuntimeException("HTTP request failed with status code '$statusCode'")