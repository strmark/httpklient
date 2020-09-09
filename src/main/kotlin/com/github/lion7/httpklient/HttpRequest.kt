package com.github.lion7.httpklient

import java.net.URI

data class HttpRequest(
        val method: String,
        val uri: URI,
        val headers: HttpHeaders
)