package com.github.lion7.httpklient.exception

class InformationalStatusException(statusCode: Int, body: String?) : HttpKlientException(statusCode, body)