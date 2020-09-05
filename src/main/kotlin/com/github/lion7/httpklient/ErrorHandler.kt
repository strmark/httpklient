package com.github.lion7.httpklient

import java.net.HttpURLConnection

interface ErrorHandler<T> {
    fun handle(connection: HttpURLConnection): T
}