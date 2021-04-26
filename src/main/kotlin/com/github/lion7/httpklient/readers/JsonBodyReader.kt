package com.github.lion7.httpklient.readers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import java.io.InputStream

class JsonBodyReader<T : Any>(private val t: TypeReference<T>, private val objectMapper: ObjectMapper, override val accept: String) : BodyReader<T> {

    override fun <S : InputStream> read(response: HttpResponse<S>): T =
        objectMapper.readValue(response.body, t)
}
