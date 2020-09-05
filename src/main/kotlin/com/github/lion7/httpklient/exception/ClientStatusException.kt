package com.github.lion7.httpklient.exception

class ClientStatusException(statusCode: Int, body: String?) : HttpKlientException(statusCode, body)