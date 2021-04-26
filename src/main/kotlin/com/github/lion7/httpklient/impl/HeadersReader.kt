package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.HttpHeaders
import java.io.BufferedInputStream

internal object HeadersReader {

    fun read(inputStream: BufferedInputStream): HttpHeaders {
        val headers = HttpHeaders()
        while (true) {
            val line = inputStream.readLine()
            if (line.isNullOrEmpty() || !line.contains(':')) {
                break
            }
            val (name, value) = line.split(':', limit = 2)
            val values = value.trim().split(',')
            values.forEach {
                headers.header(name, it, true)
            }
        }
        return headers
    }
}
