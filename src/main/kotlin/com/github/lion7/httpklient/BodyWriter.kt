package com.github.lion7.httpklient

import java.io.OutputStream

interface BodyWriter {

    val contentType: String

    fun write(outputStream: OutputStream)
}
