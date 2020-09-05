package com.github.lion7.httpklient

import java.io.InputStream

/**
 * Reads the received HTTP response into the desired format.
 * The reader is responsible for closing the stream.
 */
interface BodyReader<T> {
    val accept: String
    fun read(inputStream: InputStream): T
}