package com.github.lion7.httpklient.exception

class ServerStatusException(statusCode: Int, body: String?) : HttpKlientException(statusCode, body)