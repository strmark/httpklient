package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.Headers
import java.io.BufferedInputStream
import java.util.*

internal object HeadersReader {

    fun read(inputStream: BufferedInputStream): Headers {
        val scanner = Scanner(inputStream).useDelimiter("\r\n")
        val headers = Headers()
        while (scanner.hasNext()) {
            val (name, value) = scanner.nextLine().split(':', limit = 1)
            headers.header(name, value.trim())
        }
        return headers
    }
}