package com.github.lion7.httpklient.readers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import java.io.InputStream

class JsonBodyReader<T : Any>(
    private val t: TypeReference<T>,
    private val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(),
    override val accept: String = MediaTypes.APPLICATION_JSON_UTF_8
) : BodyReader<T> {

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): T = objectMapper.readValue(inputStream, t)
}
