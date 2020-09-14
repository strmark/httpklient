package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.HttpHeaders
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

internal object HeadersReader {

    fun read(inputStream: BufferedInputStream): HttpHeaders {
        val headers = HttpHeaders()
        while (true) {
            val line = inputStream.readLine()?.toString(StandardCharsets.UTF_8)
            if (line.isNullOrEmpty() || !line.contains(':')) {
                break
            }
            val (name, value) = line.split(':', limit = 2)
            headers.header(name, value.trim())
        }
        return headers
    }

    private fun InputStream.readLine(): ByteArray? {
        val buffer = ByteArrayOutputStream()
        while (true) {
            // carriage return found
            when (val ch = read()) {
                '\r'.toInt() -> {
                    // mark the current position in the stream so we can go back
                    mark(1)
                    // read the next character
                    val next = read()
                    // if it's not -1 or a newline, reset the stream to the marked position
                    if (next != -1 && next != '\n'.toInt()) {
                        reset()
                    }
                    // break the while loop
                    break
                }
                '\n'.toInt() -> {
                    // newline found, break the while loop
                    break
                }
                -1 -> {
                    // end of stream, break the while loop
                    break
                }
                else -> {
                    // write the byte to the buffer
                    buffer.write(ch)
                }
            }
        }
        return if (buffer.size() > 0) {
            buffer.toByteArray()
        } else {
            null
        }
    }
}
