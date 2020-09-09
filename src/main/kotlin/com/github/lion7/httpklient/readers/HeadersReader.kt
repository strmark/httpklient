package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.HttpHeaders
import java.io.BufferedInputStream
import java.util.Scanner

internal object HeadersReader {

    fun read(inputStream: BufferedInputStream): HttpHeaders {
        val scanner = Scanner(inputStream).useDelimiter("\r\n")
        val headers = HttpHeaders()
        while (scanner.hasNext()) {
            val (name, value) = scanner.nextLine().split(':', limit = 1)
            headers.header(name, value.trim())
        }
        return headers
    }
}