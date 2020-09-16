package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.HttpHeaders
import java.io.Writer

internal object HeadersWriter {

    fun write(headers: HttpHeaders, writer: Writer) {
        headers.forEach { name, values -> values.forEach { value -> writer.write("$name: $value\r\n") } }
        writer.write("\r\n")
    }
}