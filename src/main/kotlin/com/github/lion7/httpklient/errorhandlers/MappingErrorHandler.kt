package com.github.lion7.httpklient.errorhandlers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.ErrorHandler
import java.net.HttpURLConnection

class MappingErrorHandler<T>(private val bodyReader: BodyReader<T>) : ErrorHandler<T> {

    override fun handle(connection: HttpURLConnection): T = bodyReader.read(connection.errorStream)
}