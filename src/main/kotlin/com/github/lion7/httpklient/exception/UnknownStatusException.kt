package com.github.lion7.httpklient.exception

import com.github.lion7.httpklient.Headers

class UnknownStatusException(statusCode: Int, headers: Headers, body: Any?) :
    HttpKlientException(statusCode, headers, body)