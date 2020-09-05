package com.github.lion7.httpklient.exception

class RedirectStatusException(statusCode: Int, body: String?) : HttpKlientException(statusCode, body)